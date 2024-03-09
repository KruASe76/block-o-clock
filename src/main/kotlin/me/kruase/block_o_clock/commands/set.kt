package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.*
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import org.bukkit.command.CommandSender
import java.time.LocalTime


fun set(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("set")) throw UnsupportedOperationException()

    assert(args.size == 3)

    sender.sendMessage(  // TODO: test with nulls
        when (SetOption.valueOf(args[0])) {
            SetOption.TIME ->
                userConfig.messages.info["clock-set-time"]
                    ?.replace("{time}", args[2])
                    ?.replace(
                        "{clock}",
                        BOCClockManager.setTime(args[1].toInt(), LocalTime.parse(args[2]).normalized)
                    )
            SetOption.DIRECTION ->
                userConfig.messages.info["clock-set-direction"]
                    ?.replace("{direction}", args[2].uppercase())
                    ?.replace(
                        "{clock}",
                        BOCClockManager.setDirection(args[1].toInt(), ClockDirection.valueOf(args[2].uppercase()))
                    )
        }
    )
}
