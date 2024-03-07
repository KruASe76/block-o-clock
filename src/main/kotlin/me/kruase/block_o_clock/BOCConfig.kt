package me.kruase.block_o_clock

import org.bukkit.configuration.file.FileConfiguration
import java.io.File


data class BOCConfig(private val config: FileConfiguration) {
    val defaultFontSize = config.getInt("default-font-size")
    val listPageSize = config.getInt("list-page-size")
    val messages = MessagesConfig(config)
}


fun BlockOClock.getUserConfig(): BOCConfig {
    return try {
        saveDefaultConfig()
        reloadConfig()

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
    val help: Map<String, String> =
        config
            .getConfigurationSection("messages.help")!!
            .getKeys(false)
            .associateWith { config.getString("messages.help.$it")!! }
    val error: Map<String, String> =
        config
            .getConfigurationSection("messages.error")!!
            .getKeys(false)
            .associateWith { config.getString("messages.error.$it")!! }
    val info: Map<String, String> =
        config
            .getConfigurationSection("messages.info")!!
            .getKeys(false)
            .associateWith { config.getString("messages.info.$it")!! }
    val dimension: Map<String, String> =
        config
            .getConfigurationSection("messages.dimension")!!
            .getKeys(false)
            .associateWith { config.getString("messages.dimension.$it")!! }
}
