package me.kruase.block_o_clock

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.ChatColor


class BlockOClock : JavaPlugin() {
    companion object {
        lateinit var instance: BlockOClock
        lateinit var userConfig: BOCConfig

        fun sendGlobalMessage(message: String?) {
            if (message == null) return
            instance.server.onlinePlayers.forEach {
                it.sendMessage(
                    "${ChatColor.GOLD}[${ChatColor.GREEN}BlockOClock${ChatColor.GOLD}]${ChatColor.RESET} $message"
                )
            }
        }
    }

    override fun onEnable() {
        instance = this
        userConfig = getUserConfig()

        getCommand("blockoclock")!!.setExecutor(BOCCommands())
    }
}
