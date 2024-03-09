package me.kruase.block_o_clock

import org.bukkit.plugin.java.JavaPlugin


class BlockOClock : JavaPlugin() {
    companion object Static {
        lateinit var instance: BlockOClock
        lateinit var userConfig: BOCConfig
    }

    override fun onEnable() {
        instance = this
        userConfig = getUserConfig()

        getCommand("blockoclock")!!.setExecutor(BOCCommands())

        server.pluginManager.registerEvents(BOCEvents(), instance)

        BOCClockManager.run()
    }
}
