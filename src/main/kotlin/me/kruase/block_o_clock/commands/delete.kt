package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.BOCClockManager
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun delete(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("delete")) throw UnsupportedOperationException()

    if (args.size < 1) throw IllegalArgumentException()

    try {
        sender.sendMessage(
            userConfig.messages.info["clock-created"]
                ?.replace("{clock}", BOCClockManager.delete(args[0].toInt()))
        )
    } catch (e: IllegalArgumentException) {
        throw IllegalStateException(
            userConfig.messages.error["nonexistent-clock"] ?: "Error: nonexistent-clock"
        )
    }
}