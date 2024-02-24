package me.kruase.kotlin_plugin_template

import java.io.File
import org.bukkit.configuration.file.FileConfiguration


val allPaths = listOf(
    "messages.error.no-permission",
    "messages.error.invalid-command",
    "messages.error.player-only",
    "messages.help.header",
    "messages.help.help",
    "messages.help.reload",
)


data class TemplateConfig(private val config: FileConfiguration) {
    val messages = MessagesConfig(config)
}


fun Template.getUserConfig(): TemplateConfig {
    return try {
        saveDefaultConfig()
        reloadConfig()

        if ((allPaths - config.getKeys(true)).isNotEmpty()) throw NullPointerException()

        TemplateConfig(config)
    } catch (e: Exception) {
        when (e) {
            is NullPointerException, is NumberFormatException -> {
                newDefaultConfig()
                TemplateConfig(config)
            }
            else -> throw e
        }
    }.also { logger.info("Config loaded!") }
}

fun Template.newDefaultConfig() {
    logger.severe("Invalid $name config detected! Creating a new one (default)...")
    File(dataFolder, "config.yml").renameTo(
        File(dataFolder, "config.yml.old-${System.currentTimeMillis()}")
    )
    saveDefaultConfig()
    reloadConfig()
    logger.info("New (default) config created!")
}


data class MessagesConfig(private val config: FileConfiguration) {
    val help: Map<String, String> = config.getConfigurationSection("messages.help")!!
        .getKeys(false).associateWith { config.getString("messages.help.$it")!! }
    val error: Map<String, String> = config.getConfigurationSection("messages.error")!!
        .getKeys(false).associateWith { config.getString("messages.error.$it")!! }
    val info: Map<String, String> = config.getConfigurationSection("messages.info")!!
        .getKeys(false).associateWith { config.getString("messages.info.$it")!! }
}
