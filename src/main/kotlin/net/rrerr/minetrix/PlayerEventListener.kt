package net.rrerr.minetrix

import de.msrd0.matrix.client.event.TextMessageContent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEventListener(plugin: Main) : ListenerBase(plugin) {
    @EventHandler
    fun playerJoined(ev: PlayerJoinEvent) {
        if (ev.joinMessage == null) {
            return
        }

        val message = "[+] ${ChatColor.stripColor(ev.joinMessage!!)}"
        sendMessage(TextMessageContent(message))
    }

    @EventHandler
    fun playerQuit(ev: PlayerQuitEvent) {
        if (ev.quitMessage == null) {
            return
        }

        val message = "[-] ${ChatColor.stripColor(ev.quitMessage!!)}"
        sendMessage(TextMessageContent(message))
    }

    @EventHandler
    fun playerDied(ev: PlayerDeathEvent) {
        if (ev.deathMessage == null) {
            return
        }

        val message = "☠ ${ChatColor.stripColor(ev.deathMessage!!)}"
        sendMessage(TextMessageContent(message))
    }
}
