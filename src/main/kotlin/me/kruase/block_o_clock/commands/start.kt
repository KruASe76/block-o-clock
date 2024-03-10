package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.BOCClockManager
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun start(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("start")) throw UnsupportedOperationException()

    require(args.size == 1)

    BOCClockManager.startClock(args[0].toInt())
        .let { clockString ->
            userConfig.messages.info["clock-started"]
                ?.replace("{clock}", clockString)
                ?.let { sender.sendMessage(it) }
        }
}
