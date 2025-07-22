// Author: XuanLam
// Made by XuanLam with love <3

package me.xuanlam2007

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

class AttackCooldown(
    private val threshold: Int,
    private val messageIntervalMillis: Long,
    val fatigueDurationTicks: Int
) {
    private var fails = 0
    private var disarmStart: Long = 0
    private var lastMessageTime: Long = 0

    fun recordFail() = ++fails
    fun shouldApplyFatigue() = fails >= threshold
    fun resetFails() { fails = 0 }

    fun startDisarm() {
        disarmStart = System.currentTimeMillis()
        lastMessageTime = 0
    }

    fun isDisarmed(): Boolean {
        return System.currentTimeMillis() - disarmStart < messageIntervalMillis
    }

    fun trySendDisarmMessage(player: Player, rawMessage: String) {
        val now = System.currentTimeMillis()
        if (now - lastMessageTime >= messageIntervalMillis) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage))
            lastMessageTime = now
        }
    }
}
