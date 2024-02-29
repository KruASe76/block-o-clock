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
        // ...
    ): Int {
        return 0
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


abstract class BOCClock {
    abstract val location: Location
    protected abstract val widthDirection: AxisDirection
    protected abstract val heightDirection: AxisDirection
    protected abstract val timeFormatter: DateTimeFormatter
    protected abstract val foregroundMaterial: Material
    protected abstract val backgroundMaterial: Material
    protected abstract val fontType: FontType
    protected abstract val fontSize: Int

    abstract var isRunning: Boolean

    private var display: String = ""

    abstract fun update()

    fun update(time: LocalTime) {
        if (!isRunning) return

        val newDisplay = time.format(timeFormatter)

        if (display == newDisplay) return

        TODO()

        display = newDisplay
    }

    fun destroy() {
        TODO()
    }
}


class SyncedClock(
    override val location: Location,
    override val widthDirection: AxisDirection,
    override val heightDirection: AxisDirection,
    override val timeFormatter: DateTimeFormatter,
    override val foregroundMaterial: Material,
    override val backgroundMaterial: Material,
    override val fontType: FontType,
    override val fontSize: Int = 3,

    private val timeZoneId: ZoneId
) : BOCClock() {
    override var isRunning: Boolean = true

    override fun update() {
        super.update(LocalTime.now(timeZoneId))
    }
}

class NonSyncedClock(
    override val location: Location,
    override val widthDirection: AxisDirection,
    override val heightDirection: AxisDirection,
    override val timeFormatter: DateTimeFormatter,
    override val foregroundMaterial: Material,
    override val backgroundMaterial: Material,
    override val fontType: FontType,
    override val fontSize: Int = 3
) : BOCClock() {
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
}


@JvmInline
value class AxisDirection(private val direction: Pair<Char, Char>)

enum class ClockDirection { UP, DOWN }

enum class FontType { DIGITAL, MINECRAFT }

enum class ListSorting { ORDERED, NEAREST }
