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

        // Block non-ops right away
        if (!sender.isOp) {
            val noPermRaw = plugin.config.getString("permissionDeniedMessage")
                ?: "&cYou don’t have permission."
            sender.sendMessage(colorSerializer.deserialize(noPermRaw))
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            "reload" -> {
                plugin.reloadConfig()
                sender.sendMessage("§aRPGAttackSystem config reloaded.")
            }
            "info" -> {
                sendInfo(sender)
            }
            else -> {
                sender.sendMessage("§eUsage: /ras <reload|info>")
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String> {
        if (!cmd.name.equals("rpgattacksystem", ignoreCase = true)) return emptyList()
        if (!sender.isOp) return emptyList()

        return if (args.size == 1) {
            listOf("reload", "info")
                .filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
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
}
