package me.kruase.block_o_clock

import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil


object BOCClockManager {
    private val clocks = mutableMapOf<Int, BOCClock>()
    private var taskId = 0

    fun run() {
        taskId = instance.server.scheduler.scheduleSyncRepeatingTask(
            instance,
            Runnable(::update),
            0L, 1L
        )
    }

    fun stop() {
        instance.server.scheduler.cancelTask(taskId)

        if (userConfig.deleteOnDisable) clocks.values.forEach(BOCClock::destroy)
    }

    private fun update() {
        clocks.values.forEach(BOCClock::update)
    }

    fun createClock(
        location: Location,
        widthDirection: AxisDirection,
        heightDirection: AxisDirection,
        timeFormatter: DateTimeFormatter,
        timeZoneId: ZoneId?,
        foregroundMaterial: Material?,
        backgroundMaterial: Material?,
        fontType: FontType,
        fontSize: Int
    ): String {
        clocks.values.firstOrNull { it.location == location }
            ?.let {
                throw IllegalStateException(
                    (
                        userConfig.messages.error["another-clock-at-location"]
                            ?: "Error: another-clock-at-location: ID {id}"
                    )
                        .replace("{id}", it.id.toString())
                )
            }

        ((clocks.keys.maxOrNull() ?: -1) + 1)
            .let { newId ->
                when (timeZoneId) {
                    null ->
                        NonSyncedClock(
                            newId,
                            location, widthDirection, heightDirection, timeFormatter,
                            foregroundMaterial, backgroundMaterial, fontType, fontSize
                        )
                    else ->
                        SyncedClock(
                            newId,
                            location, widthDirection, heightDirection, timeFormatter,
                            foregroundMaterial, backgroundMaterial, fontType, fontSize,
                            timeZoneId
                        )
                }
                    .let { newClock ->
                        clocks[newId] = newClock
                        return newClock.fancyString
                    }
            }
    }

    fun deleteClock(id: Int): String {
        assertIdExists(id)

        clocks[id]!!.destroy()
        clocks.remove(id).let { return it!!.fancyString }
    }

    fun listClocks(sorting: ListSorting, page: Int, baseLocation: Location): Pair<List<String>, Int> {
        return Pair(
            when(sorting) {
                ListSorting.ORDERED -> clocks.values.toList()
                ListSorting.NEAREST ->
                    clocks.values.sortedBy { clock -> baseLocation.distanceSquared(clock.location) }
            }
                .drop(userConfig.listPageSize * (page - 1))
                .take(userConfig.listPageSize)
                .map(BOCClock::fancyString),
            ceil(clocks.size / userConfig.listPageSize.toDouble()).toInt()  // total pages
        )
    }

    fun startClock(id: Int): String {
        assertIdExists(id)

        clocks[id]!!.let {
            it.isRunning = true

            return it.fancyString
        }
    }

    fun stopClock(id: Int): String {
        assertIdExists(id)

        clocks[id]!!.let {
            it.isRunning = false

            return it.fancyString
        }
    }

    fun setTimeOnClock(id: Int, time: LocalTime): String {
        assertIdExists(id)

        clocks[id]!!.let {
            if (it !is NonSyncedClock)
                throw IllegalStateException(
                    userConfig.messages.error["synced-clock-set"] ?: "Error: synced-clock-set"
                )

            it.time = time

            return it.fancyString
        }
    }

    fun setDirectionOnClock(id: Int, direction: ClockDirection): String {
        assertIdExists(id)

        clocks[id]!!.let {
            if (it !is NonSyncedClock)
                throw IllegalStateException(
                    userConfig.messages.error["synced-clock-set"] ?: "Error: synced-clock-set"
                )

            it.direction = direction

            return it.fancyString
        }
    }

    fun isInClock(location: Location): Boolean =
        clocks.values.any { it.isInClock(location) }

    private fun assertIdExists(id: Int) {
        if (id !in clocks)
            throw IllegalStateException(
                userConfig.messages.error["nonexistent-clock"] ?: "Error: nonexistent-clock"
            )
    }
}


abstract class BOCClock(
    val id: Int,
    val location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    private val timeFormatter: DateTimeFormatter,
    private val foregroundMaterial: Material?,
    private val backgroundMaterial: Material?,
    fontType: FontType,
    fontSize: Int
) {
    abstract var isRunning: Boolean

    private var display: String = LocalTime.MIN.format(timeFormatter)
    private var grid: List<List<Boolean?>> =
        display.count { it.isDigit() }
            .let { digitCount ->
                when(fontType) {
                    // display width: digits + delimiters (1 pixel each) + spaces between characters
                    FontType.DIGITAL ->
                        List(
                            digitCount * fontSize + (display.length - digitCount) + (display.length - 1)
                        ) { List(fontSize * 2 - 1) { null } }
                    FontType.MINECRAFT ->
                        List(
                            digitCount * BOCRender.MINECRAFT_FONT_WIDTH + (display.length - digitCount) +
                                    (display.length - 1)
                        ) { List(BOCRender.MINECRAFT_FONT_HEIGHT) { null } }
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
                location
                    .clone()
                    .apply {
                        add(
                            widthOffsetLocation
                                .clone()
                                .apply { multiply(widthOffset.toDouble()) }
                        )
                        add(
                            heightOffsetLocation
                                .clone()
                                .apply { multiply(heightOffset.toDouble()) }
                        )
                    }
            }
        }
    }

    private val font: Map<Char, List<List<Boolean>>> =
        when(fontType) {
            FontType.DIGITAL -> BOCRender.digitalFont(fontSize)
            FontType.MINECRAFT -> BOCRender.minecraftFont
        }

    abstract fun update()

    protected fun update(time: LocalTime, isInitial: Boolean) {
        val newDisplay = time.format(timeFormatter)

        if (display == newDisplay && !isInitial) return

        val newGrid =
            newDisplay
                .map { char -> font[char]!! }
                .reduce { list1, list2 -> list1 + listOf(List(grid[0].size) { false }) + list2 }
                // listOf(...) is the space between characters

        blockLocations.forEachIndexed { width, locationList ->
            locationList.forEachIndexed { height, location ->
                if (grid[width][height] != newGrid[width][height])  // update only necessary blocks for optimization
                    (if (newGrid[width][height]) foregroundMaterial else backgroundMaterial)
                        .let { material ->
                            if (material != null)
                                location.block.type = material
                        }
            }
        }

        display = newDisplay
        grid = newGrid
    }

    fun destroy() =
        blockLocations.flatten().forEach { it.block.type = Material.AIR }

    fun isInClock(location: Location): Boolean =
        location in blockLocations.flatten()
}


class SyncedClock(
    id: Int,
    location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    timeFormatter: DateTimeFormatter,
    foregroundMaterial: Material?,
    backgroundMaterial: Material?,
    fontType: FontType,
    fontSize: Int,

    private val timeZoneId: ZoneId
) : BOCClock(
    id,
    location, widthDirection, heightDirection, timeFormatter,
    foregroundMaterial, backgroundMaterial, fontType, fontSize
) {
    override var isRunning: Boolean = true

    override fun update() {
        if (!isRunning) return

        super.update(time = LocalTime.now(timeZoneId).ticks, isInitial = false)
    }

    init {
        super.update(time = LocalTime.now(timeZoneId).ticks, isInitial = true)
    }
}

class NonSyncedClock(
    id: Int,
    location: Location,
    widthDirection: AxisDirection,
    heightDirection: AxisDirection,
    timeFormatter: DateTimeFormatter,
    foregroundMaterial: Material?,
    backgroundMaterial: Material?,
    fontType: FontType,
    fontSize: Int,
) : BOCClock(
    id,
    location, widthDirection, heightDirection, timeFormatter,
    foregroundMaterial, backgroundMaterial, fontType, fontSize
) {
    override var isRunning: Boolean = false

    var time: LocalTime = LocalTime.MIN
        set(value) {
            field = value
            super.update(time = time, isInitial = false)
        }
    var direction: ClockDirection = ClockDirection.UP

    override fun update() {
        if (!isRunning) return

        time = when(direction) {
            ClockDirection.UP -> time.plusTicks(1L)
            ClockDirection.DOWN -> time.minusTicks(1L)
        }
    }

    init {
        super.update(time = time, isInitial = true)  // to display zeros after being created
    }
}


val BOCClock.fancyString
    get() = "${ChatColor.GRAY}ID ${ChatColor.WHITE}$id${ChatColor.GRAY}: ${ChatColor.RESET}${location.fancyString}"


@JvmInline
value class AxisDirection(private val direction: Pair<Sign, Axis>) {
    val sign: Sign
        get() = direction.first

    val axis: Axis
        get() = direction.second
}

enum class Sign(val raw: Int) {
    PLUS(1), MINUS(-1);

    companion object {
        infix fun from(value: Char): Sign {
            return when(value) {
                '+' -> PLUS
                '-' -> MINUS
                else -> throw IllegalArgumentException()
            }
        }

        val options = listOf("+", "-")
    }
}

enum class Axis { X, Y, Z }

enum class ClockDirection { UP, DOWN }

enum class FontType { DIGITAL, MINECRAFT }

enum class ListSorting { ORDERED, NEAREST }
