package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.*
import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.DateTimeException
import java.time.ZoneId
import java.time.format.DateTimeFormatter


val clockSections = listOf("HH", "mm", "ss", "SS")
val clockSectionNames = listOf("hour", "min", "sec", "tick")


// boc create <dim> <xyz> ±<x|y|z> ±<x|y|z> <hour|min|sec|tick> <hour|min|sec|tick> <±hh[:mm]|none> <block|none> <block|none> <font> [size=3]
fun create(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("create")) throw UnsupportedOperationException()

    assert(args.size in 12..13)

    assert(args[6] in clockSectionNames && args[7] in clockSectionNames)

    try {
        val coords: List<Double> =
            when (sender) {
                is Player ->
                    args
                        .slice(1..3)
                        .zip(sender.location.run { listOf(blockX, blockY, blockZ) })
                        .map {
                            if (it.first.startsWith("~"))
                                it.second + if (it.first == "~") .0 else it.first.drop(1).toDouble()
                            else it.first.toDouble()
                        }
                else -> args.slice(1..3).map { it.toDouble() }
            }

        val location = Location(
            instance.server.worlds.first { it.environment == environmentByNormalName(args[0].uppercase()) },
            coords[0], coords[1], coords[2]
        )
        assert(location.world!!.worldBorder.isInside(location))

        val widthDirection = AxisDirection(Pair(Sign from args[4][0], Axis.valueOf(args[4][1].uppercase())))
        val heightDirection = AxisDirection(Pair(Sign from args[5][0], Axis.valueOf(args[5][1].uppercase())))
        assert(widthDirection.axis != heightDirection.axis)

        val timeFormatter = DateTimeFormatter.ofPattern(
            clockSections
                .slice(clockSectionNames.indexOf(args[6])..clockSectionNames.indexOf(args[7]))
                .also { assert(it.isNotEmpty()) }
                .joinToString(":")
                .replace(":SS", ".SS")
        )

        val timeZoneId = if (args[8] == "null") null else ZoneId.of(args[8])

        val foregroundMaterial =
            if (args[9] == "null") null else Material.matchMaterial(args[9]) ?: throw AssertionError()
        val backgroundMaterial =
            if (args[10] == "null") null else Material.matchMaterial(args[10]) ?: throw AssertionError()

        val fontType = FontType.valueOf(args[11].uppercase())
        val fontSize = args.getOrNull(12)?.toInt() ?: userConfig.defaultFontSize

        sender.sendMessage(
            userConfig.messages.info["clock-created"]
                ?.replace(
                    "{clock}",
                    BOCClockManager.create(
                        location, widthDirection, heightDirection, timeFormatter, timeZoneId,
                        foregroundMaterial, backgroundMaterial, fontType, fontSize
                    )
                )
        )
    } catch (e: Exception) {
        when (e) {
            is NumberFormatException, is NoSuchElementException, is DateTimeException,
            is IllegalArgumentException, is IllegalStateException, is NullPointerException -> throw AssertionError()
            // theoretically NullPointerException should never occur
            else -> throw e  // including AssertionError
        }
    }
}
