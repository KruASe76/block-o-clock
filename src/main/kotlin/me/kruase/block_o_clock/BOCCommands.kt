package me.kruase.block_o_clock

import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.commands.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.time.DateTimeException
import java.time.ZoneId
import java.time.format.DateTimeParseException
import kotlin.math.ceil


class BOCCommands : TabExecutor {
    private val digitOptions = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val digitOptionsExtended = listOf("", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val listSortingOptions = ListSorting.entries.map { it.name.lowercase() }
    private val dimensionOptions = normalNameToEnvironment.keys.toList()
    private val signOptions = Sign.options
    private val axisOptions = Axis.entries.map { it.name.lowercase() }
    private val clockSectionOptions = clockSections
    private val blockOptions =
        Material.entries
            .filter { it.isBlock && "LEGACY" !in it.name }
            .map { it.name.lowercase() }
            .sorted()
    private val fontTypeOptions = FontType.entries.map { it.name.lowercase() }
    private val setOptions = SetOption.entries.map { it.name.lowercase() }
    private val clockDirectionOptions = ClockDirection.entries.map { it.name.lowercase() }
    private val defaultTimePattern = listOf("00:", "00:", "00.", "00")
    private val timeCompletionStep = 3

    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): List<String> {
        val fullArgs = args.dropLast(1)

        return when (fullArgs.getOrNull(0)) {
            null ->
                userConfig.messages.help.keys
                    .filter { sender.hasPluginPermission(it.split("-")[0]) } - "header"
            "help" ->
                if (!sender.hasPluginPermission(args[0])) emptyList()
                else when (fullArgs.getOrNull(1)) {
                    null ->
                        userConfig.messages.help.keys
                            .filter { sender.hasPluginPermission(it.split("-")[0]) } - "header"
                    else -> emptyList()
                }
            "list" ->
                if (!sender.hasPluginPermission(args[0])) emptyList()
                else when (fullArgs.getOrNull(1)) {
                    null -> listSortingOptions
                    in listSortingOptions ->
                        if (fullArgs.getOrNull(2) == null) digitOptionsExtended.map { args[2] + it } - ""
                        else emptyList()
                    else -> emptyList()
                }
            "create" ->
                if (!sender.hasPluginPermission(args[0])) emptyList()
                else when (fullArgs.getOrNull(1)) {
                    null -> dimensionOptions
                    in dimensionOptions ->
                        if (!fullArgs.drop(2).takeLast(3).all { it.toIntOrNull() != null })
                            emptyList()
                        else when (fullArgs.getOrNull(4)) {
                            null ->
                                when (sender) {
                                    is Player -> targetBlockCompletion(sender, fullArgs.drop(2).takeLast(3))
                                    else -> emptyList()
                                }
                            else ->
    when (fullArgs.getOrNull(5)) {
        null ->
            when (args[5].length) {
                0 -> signOptions
                1, 2 ->
                    if (args[5][0].toString() in signOptions) axisOptions
                    else emptyList()
                else -> emptyList()
            }
        else ->
            if (!(args[5].length == 2 && args[5][0].toString() in signOptions && args[5][1].toString() in axisOptions))
                emptyList()
            else when (fullArgs.getOrNull(6)) {
                null ->
                    when (args[6].length) {
                        0 -> signOptions
                        1, 2 ->
                            if (args[6][0].toString() in signOptions) axisOptions
                            else emptyList()
                        else -> emptyList()
                    }
                else ->
                    if (
                        !(
                            args[6].length == 2 &&
                            args[6][0].toString() in signOptions && args[6][1].toString() in axisOptions
                        )
                    )
                        emptyList()
                    else when (fullArgs.getOrNull(7)) {
                        null -> clockSectionOptions
                        in clockSectionOptions ->
                            when (fullArgs.getOrNull(8)) {
                                null -> clockSectionOptions
                                in clockSectionOptions ->
    when (fullArgs.getOrNull(9)) {
        null ->
            when (args[9].length) {
                0 -> signOptions + "none"
                1 ->
                    if (args[9][0].toString() in signOptions || "none".startsWith(args[9]))
                        digitOptions.take(2) + "none"  // suggesting only 0 and 1
                    else emptyList()
                2 ->
                    if (
                        args[9][0].toString() in signOptions && args[9][1].toString() in digitOptions ||
                        "none".startsWith(args[9])
                    )
                        digitOptions + "none"
                    else emptyList()
                3, 4, 5 ->
                    if (
                        args[9][0].toString() in signOptions &&
                        args[9][1].toString() in digitOptions && args[9][2].toString() in digitOptions ||
                        "none".startsWith(args[9])
                    )
                        listOf(":15", ":30", ":45") + "none"  // mostly used
                    else emptyList()
                else -> emptyList()
            }
        else ->
            try {
                if (args[9] != "none") ZoneId.of(args[9])

                when (fullArgs.getOrNull(10)) {
                    null -> blockOptions + "none"
                    in blockOptions + "none" ->
                        when (fullArgs.getOrNull(11)) {
                            null -> blockOptions + "none"
                            in blockOptions + "none" ->
                                when (fullArgs.getOrNull(12)) {
                                    null -> fontTypeOptions
                                    FontType.DIGITAL.name.lowercase() ->
                                        if (fullArgs.getOrNull(13) == null)
                                            digitOptionsExtended.map { args[13] + it } - ""
                                    else emptyList()
                                    else -> emptyList()
                                }
                            else -> emptyList()
                        }
                    else -> emptyList()
                }
            } catch (e: DateTimeParseException) {
                emptyList()
            }
    }
                                else -> emptyList()
                            }
                        else -> emptyList()
                    }
            }
    }
                        }
                    else -> emptyList()
                }
            "delete", "start", "stop" ->
                if (!sender.hasPluginPermission(args[0])) emptyList()
                else if (fullArgs.getOrNull(1) == null) digitOptionsExtended.map { args[1] + it } - ""
                else emptyList()
            "set" ->
                if (!sender.hasPluginPermission(args[0])) emptyList()
                else when (fullArgs.getOrNull(1)) {
                    null -> setOptions
                    SetOption.TIME.name.lowercase() ->
                        if (fullArgs.getOrNull(2) == null)
                            try {
                                if (  // the magic starts TODO: test this!!!
                                    Regex(
                                        defaultTimePattern
                                            .joinToString("")
                                            .slice(args[2].indices)
                                            .replace("0", "\\d")
                                            .replace(".", "\\.")
                                    )
                                        .matches(args[2])
                                )
                                    listOf(
                                        args[2] +
                                                defaultTimePattern
                                                    .take(args[2].length / timeCompletionStep)
                                                    .joinToString("")
                                                    .drop(args[2].length)
                                    )
                                else emptyList()
                            } catch (e: IndexOutOfBoundsException) {
                                emptyList()
                            }
                        else emptyList()
                    SetOption.DIRECTION.name.lowercase() ->
                        if (fullArgs.getOrNull(2) == null) clockDirectionOptions
                        else emptyList()
                    else -> emptyList()
                }
            else -> emptyList()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            when (args.getOrNull(0)) {
                null -> help(sender, emptyList())
                "help" -> help(sender, args.drop(1))
                "list" -> list(sender, args.drop(1))
                "create" -> create(sender, args.drop(1))
                "delete" -> delete(sender, args.drop(1))
                "start" -> start(sender, args.drop(1))
                "stop" -> stop(sender, args.drop(1))
                "set" -> set(sender, args.drop(1))
                "reload" -> {
                    if (!sender.hasPluginPermission("reload")) throw UnsupportedOperationException()

                    userConfig = instance.getUserConfig()
                }
                else -> throw AssertionError()
            }
        } catch (e: Throwable) {
            when (e) {
                is AssertionError, is IllegalArgumentException, is NoSuchElementException, is DateTimeException ->
                    sender.sendMessage(
                        "${ChatColor.RED}${userConfig.messages.error["invalid-command"] ?: "Error: invalid-command"}"
                    )
                is UnsupportedOperationException ->
                    sender.sendMessage(
                        "${ChatColor.RED}${userConfig.messages.error["no-permission"] ?: "Error: no-permission"}"
                    )
                is IllegalStateException -> sender.sendMessage("${ChatColor.RED}${e.message ?: "Unknown error"}")
                else -> throw e
            }
        }

        return true
    }

    // trying to recreate target block coordinates completion (like in /setblock)
    private fun targetBlockCompletion(player: Player, args: List<String>): List<String> {
        // already typed in coordinates args amount -> index
        val base: List<List<String>> = player.getTargetBlockExact(8)
            ?.run {
                listOf(listOf("$x", "$y", "$z"), listOf("$y", "$z"), listOf("$z"))
            } ?: listOf(listOf("~", "~", "~"), listOf("~", "~"), listOf("~"))

        val fullCoords = when (val current = args.last()) {
            "" -> base.getOrNull(args.size - 1) ?: emptyList()
            else -> when (args.size) {
                in 1..3 -> listOf(current) + base[args.size - 1].drop(1)
                else -> emptyList()
            }
        }

        return listOf(
            fullCoords.joinToString(" "),
            fullCoords.dropLast(1).joinToString(" "),
            fullCoords.dropLast(2).joinToString(" ")
        ).filter { it.isNotEmpty()}
    }
}


enum class SetOption { TIME, DIRECTION }
