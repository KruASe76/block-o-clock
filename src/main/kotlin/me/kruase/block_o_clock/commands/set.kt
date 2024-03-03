package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun set(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("set")) throw UnsupportedOperationException()
}
