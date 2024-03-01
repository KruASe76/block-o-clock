package me.kruase.block_o_clock

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object BOCClockManager {
    private val clocks = mutableListOf<BOCClock>()

    fun create(
        location: Location,
        widthDirection: AxisDirection,
        heightDirection: AxisDirection,
        timeFormatter: DateTimeFormatter,
        timeZoneId: ZoneId?,
        foregroundMaterial: Material,
        backgroundMaterial: Material,
        fontType: FontType,
        fontSize: Int,
    ): Int {
        clocks.add(
            when (timeZoneId) {
                null -> NonSyncedClock(
                    location, widthDirection, heightDirection, timeFormatter,
                    foregroundMaterial, backgroundMaterial, fontType, fontSize
                )
                else -> SyncedClock(
                    location, widthDirection, heightDirection, timeFormatter,
                    foregroundMaterial, backgroundMaterial, fontType, fontSize,
                    timeZoneId
                )
            }
        )
        return clocks.size - 1
    }

    fun delete(id: Int) {
        clocks[id].destroy()
        clocks.removeAt(id)
    }

    fun update() {
        clocks.forEach(BOCClock::update)
    }

    fun list(
        sorting: ListSorting,
        page: Int,
        baseLocation: Location = BlockOClock.instance.server.worlds[0].spawnLocation
    ): List<String> {
        return when(sorting) {
            ListSorting.ORDERED -> clocks
            ListSorting.NEAREST -> clocks.sortedBy { clock -> baseLocation.distanceSquared(clock.location) }
        }
            .slice(BlockOClock.userConfig.listPageSize * (page - 1) until BlockOClock.userConfig.listPageSize * page)
            .mapIndexed { index, clock ->
                "${ChatColor.GRAY}ID ${ChatColor.WHITE}$index${ChatColor.GRAY}: " +
                        "${ChatColor.RESET}${clock.location.fancyString}"
            }
    }

    fun start(id: Int) {
        clocks[id].isRunning = true
    }

    fun stop(id: Int) {
        clocks[id].isRunning = false
    }

    fun setTime(id: Int, time: LocalTime) {
        clocks[id].let {
            if (it !is NonSyncedClock) throw IllegalStateException(
                BlockOClock.userConfig.messages.error["synced-clock-set"] ?: "Error: synced-clock-set"
            )

            it.time = time
        }
    }

    fun setDirection(id: Int, direction: ClockDirection) {
        clocks[id].let {
            if (it !is NonSyncedClock) throw IllegalStateException(
                BlockOClock.userConfig.messages.error["synced-clock-set"] ?: "Error: synced-clock-set"
            )

            it.direction = direction
        }
    }
}


abstract class BOCClock(
    val location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    private val timeFormatter: DateTimeFormatter,
    private val foregroundMaterial: Material,
    private val backgroundMaterial: Material,
    fontType: FontType,
    fontSize: Int
) {
    abstract var isRunning: Boolean

    private var display: String = LocalTime.MIN.format(timeFormatter)
    private var grid: List<List<Boolean>> =
        display.count { it.isDigit() }.let { digitCount ->
            when(fontType) {
                // display width: digits + delimiters (1 pixel each) + spaces between characters
                FontType.DIGITAL -> List(
                    digitCount * fontSize + (display.length - digitCount) + (display.length - 1)
                ) { List(fontSize * 2 - 1) { false } }
                FontType.MINECRAFT -> List(
                    digitCount * BOCRender.MINECRAFT_FONT_WIDTH + (display.length - digitCount) + (display.length - 1)
                ) { List(BOCRender.MINECRAFT_FONT_HEIGHT) { false } }
            }
        }

    private val blockLocations: List<List<Location>> = run {
        val widthOffsetLocation = Location(
            location.world,
            (if (widthDirection.axis == Axis.X) widthDirection.sign.raw else 0).toDouble(),
            (if (widthDirection.axis == Axis.Y) widthDirection.sign.raw else 0).toDouble(),
            (if (widthDirection.axis == Axis.Z) widthDirection.sign.raw else 0).toDouble()
        )
        val heightOffsetLocation = Location(
            location.world,
            (if (heightDirection.axis == Axis.X) heightDirection.sign.raw else 0).toDouble(),
            (if (heightDirection.axis == Axis.Y) heightDirection.sign.raw else 0).toDouble(),
            (if (heightDirection.axis == Axis.Z) heightDirection.sign.raw else 0).toDouble()
        )

        List(grid.size) { widthOffset ->
            List(grid[0].size) { heightOffset ->
                location  // TODO: test if works as intended
                    .clone()
                    .add(widthOffsetLocation.multiply(widthOffset.toDouble()))
                    .add(heightOffsetLocation.multiply(heightOffset.toDouble()))
            }
        }
    }

    private val font: Map<Char, List<List<Boolean>>> =
        when(fontType) {
            FontType.DIGITAL -> BOCRender.digitalFont(fontSize)
            FontType.MINECRAFT -> BOCRender.minecraftFont
        }

    abstract fun update()

    protected fun update(time: LocalTime) {
        if (!isRunning) return

        val newDisplay = time.format(timeFormatter)

        if (display == newDisplay) return

        val newGrid = display
            .map { char -> font[char]!! }
            .fold(emptyList<List<Boolean>>()) { list1, list2 -> list1 + listOf(List(grid[0].size) { false }) + list2 }

        blockLocations.forEachIndexed { width, locationList ->
            locationList.forEachIndexed { height, location ->
                if (grid[width][height] != newGrid[width][height])  // update only necessary blocks for optimization
                    location.block.type = if (newGrid[width][height]) foregroundMaterial else backgroundMaterial
            }
        }

        display = newDisplay
        grid = newGrid
    }

    fun destroy() {
        blockLocations.flatten().forEach { it.block.type = Material.AIR }
    }
}


class SyncedClock(
    location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    timeFormatter: DateTimeFormatter,
    foregroundMaterial: Material,
    backgroundMaterial: Material,
    fontType: FontType,
    fontSize: Int,

    private val timeZoneId: ZoneId
) : BOCClock(
    location, widthDirection, heightDirection, timeFormatter,
    foregroundMaterial, backgroundMaterial, fontType, fontSize
) {
    override var isRunning: Boolean = true

    override fun update() {
        super.update(LocalTime.now(timeZoneId))
    }

    init {
        update()
    }
}

class NonSyncedClock(
    location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    timeFormatter: DateTimeFormatter,
    foregroundMaterial: Material,
    backgroundMaterial: Material,
    fontType: FontType,
    fontSize: Int,
) : BOCClock(
    location, widthDirection, heightDirection, timeFormatter,
    foregroundMaterial, backgroundMaterial, fontType, fontSize
) {
    override var isRunning: Boolean = false

    var time: LocalTime = LocalTime.MIN
    var direction: ClockDirection = ClockDirection.UP

    override fun update() {
        time = when(direction) {
            ClockDirection.UP -> time.plusTicks(1L)
            ClockDirection.DOWN -> time.minusTicks(1L)
        }
        super.update(time)
    }

    init {
        super.update(time)  // to display zeros after being created
    }
}


@JvmInline
value class AxisDirection(private val direction: Pair<Sign, Axis>) {
    val sign: Sign
        get() = direction.first

    val axis: Axis
        get() = direction.second
}

enum class Sign(val raw: Int) { PLUS(1), MINUS(-1) }

enum class Axis { X, Y, Z }

enum class ClockDirection { UP, DOWN }

enum class FontType { DIGITAL, MINECRAFT }

enum class ListSorting { ORDERED, NEAREST }
