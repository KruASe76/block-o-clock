package me.kruase.block_o_clock

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class BOCEvents : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!BOCClockManager.isInClock(event.block.location)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBurn(event: BlockBurnEvent) {
        if (!BOCClockManager.isInClock(event.block.location)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (!BOCClockManager.isInClock(event.block.location)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockFade(event: BlockFadeEvent) {
        if (!BOCClockManager.isInClock(event.block.location)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onLeaveDecay(event: LeavesDecayEvent) {
        if (!BOCClockManager.isInClock(event.block.location)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockExplode(event: BlockPistonExtendEvent) {
        if (event.blocks.all { !BOCClockManager.isInClock(it.location) }) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockExplode(event: BlockPistonRetractEvent) {
        if (event.blocks.all { !BOCClockManager.isInClock(it.location) }) return

        event.isCancelled = true
    }
}
