package net.rrerr.minetrix

import de.msrd0.matrix.client.event.TextMessageContent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
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

    @EventHandler
    fun playerAdvancementDone(ev: PlayerAdvancementDoneEvent) {
        // We only want to forward advancements that are done, are not some new unlocked recipes,
        // and are not the start of some advancement chain.
        if (!ev.player.getAdvancementProgress(ev.advancement).isDone ||
            ev.advancement.key.key.startsWith("recipes/") ||
            ev.advancement.key.key.endsWith("/root")) {
            return
        }

        val message = "🏆 ${ev.player.name} has made the advancement ${ev.advancement.key.key}"
        sendMessage(TextMessageContent(message))
    }
}
