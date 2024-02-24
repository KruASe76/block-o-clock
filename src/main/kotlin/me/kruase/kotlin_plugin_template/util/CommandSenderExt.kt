package me.kruase.kotlin_plugin_template.util

import org.bukkit.command.CommandSender
import me.kruase.kotlin_plugin_template.Template.Companion.instance


fun CommandSender.hasPluginPermission(name: String): Boolean {
    return hasPermission("${instance.name.lowercase()}.$name")
}
