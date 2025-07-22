// Author: XuanLam
// Made by XuanLam with love <3

package me.xuanlam2007

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


class RPGAttackSystem : JavaPlugin(), Listener {
    val cooldowns = ConcurrentHashMap<UUID, AttackCooldown>()
    val console = Bukkit.getConsoleSender()
    val desc = description
    val authors = desc.authors.joinToString(", ")

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(this, this)

        val handler = CommandHandler(this)
        getCommand("rpgattacksystem")?.setExecutor(handler)
        getCommand("rpgattacksystem")?.tabCompleter = handler

        console.sendMessage("")
        console.sendMessage("§m                                                         §f")
        console.sendMessage("§a              RPGAttackSystem v${desc.version} enabled!")
        console.sendMessage("§b              Made by $authors with love <3          ")
        console.sendMessage("§m                                                         §f")
        console.sendMessage("")
    }

    override fun onDisable() {
        cooldowns.clear()
        console.sendMessage("")
        console.sendMessage("§m                                                         §f")
        console.sendMessage("§c              RPGAttackSystem v${desc.version} disabled!")
        console.sendMessage("§e                      See ya soon ;)          ")
        console.sendMessage("§m                                                         §f")
        console.sendMessage("")
    }

    @EventHandler
    fun onPreAttack(event: PrePlayerAttackEntityEvent) {
        val player = event.player as? Player ?: return
        if (player.attackCooldown < 1.0f) {
            val cd = cooldowns.computeIfAbsent(player.uniqueId) {
                AttackCooldown(
                    threshold = config.getInt("shouldApplyFatigueThreshold", 5),
                    messageIntervalMillis = config.getInt("disarmMessageIntervalSeconds", 3) * 1000L,
                    fatigueDurationTicks = config.getInt("fatigueDurationTicks", 40)
                )
            }

            if (cd.isDisarmed()) {
                cd.trySendDisarmMessage(player, config.getString("disarmMessage")!!)
                event.isCancelled = true
                return
            }

            event.isCancelled = true
            cd.recordFail()

            if (cd.shouldApplyFatigue()) {
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.MINING_FATIGUE,
                        cd.fatigueDurationTicks,
                        255, false, false, false
                    )
                )
                cd.startDisarm()
                cd.trySendDisarmMessage(player, config.getString("disarmMessage")!!)
                cd.resetFails()
            }
        }
    }
}
