// Author: XuanLam
// Made by XuanLam with love <3

package me.xuanlam2007

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CommandHandler(private val plugin: RPGAttackSystem) : CommandExecutor, TabCompleter {
    private val colorSerializer = LegacyComponentSerializer.legacyAmpersand()

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!cmd.name.equals("rpgattacksystem", ignoreCase = true)) return false

        val cfg = plugin.config
        val noPermRaw = cfg.getString("permissionDeniedMessage") ?: "&cYou don’t have permission."
        val noPerm = colorSerializer.deserialize(noPermRaw)

        return when (args.getOrNull(0)?.lowercase()) {
            "reload" -> {
                if (!sender.hasPermission("rpgattacksystem.reload")) {
                    sender.sendMessage(noPerm)
                } else {
                    plugin.reloadConfig()
                    sender.sendMessage("§aRPGAttackSystem config reloaded.")
                }
                true
            }
            "info" -> {
                if (!sender.hasPermission("rpgattacksystem.info")) {
                    sender.sendMessage(noPerm)
                } else {
                    sendInfo(sender)
                }
                true
            }
            else -> {
                sender.sendMessage("§eUsage: /ras <reload|info>")
                true
            }
        }
    }

    private fun sendInfo(sender: CommandSender) {
        val cfg = plugin.config
        val authors = plugin.description.authors.joinToString(", ")
        sender.sendMessage("§m                                                         §f")
        sender.sendMessage("")
        sender.sendMessage("    §aRPGAttackSystem §bv${plugin.description.version}")
        sender.sendMessage("    §aProudly made by §e$authors §awith love <3")
        sender.sendMessage("")
        sender.sendMessage("    §eSettings")
        sender.sendMessage("     ├─ §eThreshold: §f${cfg.getInt("shouldApplyFatigueThreshold")}")
        sender.sendMessage("     ├─ §eDisarm Message Interval: §f${cfg.getInt("disarmMessageIntervalSeconds")}s")
        sender.sendMessage("     ├─ §eFatigue Duration: §f${cfg.getInt("fatigueDurationTicks") / 20.0}s")
        sender.sendMessage("")
        sender.sendMessage("§m                                                         §f")
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String> {
        if (!cmd.name.equals("rpgattacksystem", ignoreCase = true)) return emptyList()
        if (args.size == 1) {
            return listOf("reload", "info")
                .filter { it.startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}