package me.kruase.block_o_clock

import org.bukkit.configuration.file.FileConfiguration
import java.io.File


val allPaths = listOf(
    "default-font-size",
    "list-page-size",
    "messages.info.overworld",
    "messages.info.nether",
    "messages.info.end",
    "messages.info.custom",
    "messages.info.clock-created",
    "messages.info.clock-deleted",
    "messages.info.clock-started",
    "messages.info.clock-stopped",
    "messages.info.clock-set-time",
    "messages.info.clock-set-direction",
    "messages.error.no-permission",
    "messages.error.invalid-command",
    "messages.error.nonexistent-clock",
    "messages.error.synced-clock-set",
    "messages.help.header",
    "messages.help.help",
    "messages.help.reload",
)


data class BOCConfig(private val config: FileConfiguration) {
    val defaultFontSize = config.getInt("default-font-size")
    val listPageSize = config.getInt("list-page-size")
    val messages = MessagesConfig(config)
}


fun BlockOClock.getUserConfig(): BOCConfig {
    return try {
        saveDefaultConfig()
        reloadConfig()

        if ((allPaths - config.getKeys(true)).isNotEmpty()) throw NullPointerException()

        BOCConfig(config)
    } catch (e: Exception) {
        when (e) {
            is NullPointerException, is NumberFormatException -> {
                newDefaultConfig()
                BOCConfig(config)
            }
            else -> throw e
        }
    }.also { logger.info("Config loaded!") }
}

fun BlockOClock.newDefaultConfig() {
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
