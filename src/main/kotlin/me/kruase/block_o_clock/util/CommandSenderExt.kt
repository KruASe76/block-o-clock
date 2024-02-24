package me.kruase.block_o_clock.util

import org.bukkit.command.CommandSender
import me.kruase.block_o_clock.BlockOClock.Companion.instance


fun CommandSender.hasPluginPermission(name: String): Boolean {
    return hasPermission("${instance.name.lowercase()}.$name")
}
