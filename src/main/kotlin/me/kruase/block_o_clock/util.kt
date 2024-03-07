package me.kruase.block_o_clock

import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World.Environment
import org.bukkit.command.CommandSender
import java.time.LocalTime


fun <T> List<List<T>>.transpose(): List<List<T>> {
    return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
}


const val NANO_TICK_SCALE = 10000000
const val NANOS_PER_TICK = 50000000
const val TICKS_PER_SECOND = 20
const val SECONDS_PER_MINUTE = 60
const val MINUTES_PER_HOUR = 60
const val HOURS_PER_DAY = 24
const val TICKS_PER_DAY = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY
const val TICKS_PER_HOUR = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_HOUR
const val TICKS_PER_MINUTE = TICKS_PER_SECOND * SECONDS_PER_MINUTE

fun LocalTime.plusTicks(ticksToAdd: Long): LocalTime {
    if (ticksToAdd == 0L) return this

    val currentTicks = hour * TICKS_PER_HOUR +
            minute * TICKS_PER_MINUTE +
            second * TICKS_PER_SECOND +
            nano / NANOS_PER_TICK
    val newTicks = ((ticksToAdd % TICKS_PER_DAY).toInt() + currentTicks + TICKS_PER_DAY) % TICKS_PER_DAY

    if (newTicks == currentTicks) return this

    return LocalTime.of(
        newTicks / TICKS_PER_HOUR,
        (newTicks / TICKS_PER_MINUTE) % MINUTES_PER_HOUR,
        (newTicks / TICKS_PER_SECOND) % SECONDS_PER_MINUTE,
        (newTicks % TICKS_PER_SECOND) * NANO_TICK_SCALE
    )
}

fun LocalTime.minusTicks(ticksToSubtract: Long): LocalTime {
    return plusTicks(-ticksToSubtract)
}

val LocalTime.ticks
    get() = withNano(nano / NANOS_PER_TICK * NANO_TICK_SCALE)


fun CommandSender.hasPluginPermission(name: String): Boolean {
    return hasPermission("${instance.name.lowercase()}.$name")
}


val Environment.normalName: String  // without NORMAL lol
    get() = when (this) {
        Environment.NORMAL -> "overworld"
        Environment.NETHER -> "nether"
        Environment.THE_END -> "end"
        Environment.CUSTOM -> "custom"
    }

fun environmentByNormalName(normalName: String): Environment =
    when (normalName.lowercase()) {
        "overworld" -> Environment.NORMAL
        "nether" -> Environment.NETHER
        "end" -> Environment.THE_END
        "custom" -> Environment.CUSTOM
        else -> throw IllegalArgumentException()
    }

val Location.fancyString: String  // example: Nether, XYZ: 42 69 -777
    get() =
        when (world!!.environment) {
            Environment.NORMAL -> ChatColor.GREEN
            Environment.NETHER -> ChatColor.RED
            Environment.THE_END -> ChatColor.LIGHT_PURPLE
            Environment.CUSTOM -> ChatColor.YELLOW
        }
            .let { dimensionColor ->
                "$dimensionColor${userConfig.messages.dimension[world!!.environment.normalName]}${ChatColor.GRAY}, " +
                        "${ChatColor.GOLD}X${ChatColor.AQUA}Y${ChatColor.YELLOW}Z${ChatColor.GRAY}: " +
                        "${ChatColor.GOLD}${x.toInt()} ${ChatColor.AQUA}${y.toInt()} ${ChatColor.YELLOW}${z.toInt()}" +
                        "${ChatColor.RESET}"
            }
