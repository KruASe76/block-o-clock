package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.*
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.time.LocalTime


fun set(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("set")) throw UnsupportedOperationException()

    require(args.size == 3)

    when (SetOption.valueOf(args[0].uppercase())) {
        SetOption.TIME ->
            BOCClockManager.setTimeOnClock(args[1].toInt(), LocalTime.parse(args[2]))
                .let { clockString ->
                    userConfig.messages.info["clock-set-time"]
                        ?.replace("{time}", "${ChatColor.GOLD}${args[2]}${ChatColor.RESET}")
                        ?.replace("{clock}", clockString)
                }
        SetOption.DIRECTION ->
            BOCClockManager.setDirectionOnClock(args[1].toInt(), ClockDirection.valueOf(args[2].uppercase()))
                .let { clockString ->
                    userConfig.messages.info["clock-set-direction"]
                        ?.replace("{direction}", "${ChatColor.GOLD}${args[2].uppercase()}${ChatColor.RESET}")
                        ?.replace("{clock}", clockString)
                }
    }
        ?.let { sender.sendMessage(it) }
}
