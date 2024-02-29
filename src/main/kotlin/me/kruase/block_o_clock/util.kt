package me.kruase.block_o_clock

import me.kruase.block_o_clock.BlockOClock.Companion.instance
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import java.time.LocalTime


fun String.fullTitlecase(): String {  // titlecase but all non-first letters are forced to lowercase
    return lowercase().replaceFirstChar { it.titlecase() }
}

val NANOS_PER_TICK = 10000000
val TICKS_PER_SECOND = 20
val SECONDS_PER_MINUTE = 60
val MINUTES_PER_HOUR = 60
val HOURS_PER_DAY = 24
val TICKS_PER_DAY = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY
val TICKS_PER_HOUR = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_HOUR
val TICKS_PER_MINUTE = TICKS_PER_SECOND * SECONDS_PER_MINUTE

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
        (newTicks % TICKS_PER_SECOND) * NANOS_PER_TICK
    )
}

fun LocalTime.minusTicks(ticksToSubtract: Long): LocalTime {
    return plusTicks(-ticksToSubtract)
}


fun CommandSender.hasPluginPermission(name: String): Boolean {
    return hasPermission("${instance.name.lowercase()}.$name")
}


val Location.fancyString: String  // example: Nether, XYZ: 42 69 -777
    get() {
        when (world!!.environment) {
            World.Environment.NORMAL -> ChatColor.GREEN
            World.Environment.NETHER -> ChatColor.RED
            World.Environment.THE_END -> ChatColor.LIGHT_PURPLE
            World.Environment.CUSTOM -> ChatColor.YELLOW
        }.let { dimensionColor ->
            return "$dimensionColor${world!!.environment.name.fullTitlecase()}${ChatColor.GRAY}, " +
                    "${ChatColor.GOLD}X${ChatColor.AQUA}Y${ChatColor.YELLOW}Z${ChatColor.GRAY}: " +
                    "${ChatColor.GOLD}${x.toInt()} ${ChatColor.AQUA}${y.toInt()} ${ChatColor.YELLOW}${z.toInt()}" +
                    "${ChatColor.RESET}"
        }
    }
