package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun help(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("help")) throw UnsupportedOperationException()

    if (args.size > 1) throw IllegalArgumentException()

    when (args.getOrNull(0)) {
        null -> userConfig.messages.help.keys
            .filter { sender.hasPluginPermission(it.replace("-", ".")) || it == "header"}
            .forEach { sender.sendMessage(userConfig.messages.help[it]) }
        in userConfig.messages.help.keys - "header" -> sender.sendMessage(userConfig.messages.help[args[0]])
        else -> throw IllegalArgumentException()
    }
}
