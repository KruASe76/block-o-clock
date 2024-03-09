package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.BOCClockManager
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun start(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("start")) throw UnsupportedOperationException()

    assert(args.size == 1)

    sender.sendMessage(
        userConfig.messages.info["clock-started"]
            ?.replace("{clock}", BOCClockManager.start(args[0].toInt()))
    )
}
