package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender


fun list(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("list")) throw UnsupportedOperationException()

    // wrong argument to Enum.valueOf() throws IllegalArgumentException
    // default location: instance.server.worlds[0].spawnLocation
}
