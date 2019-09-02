package net.rrerr.minetrix

import de.msrd0.matrix.client.event.TextMessageContent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

class MinecraftChatMessageListener(plugin: Main) : ListenerBase(plugin) {
    @EventHandler
    fun messageReceived(ev: AsyncPlayerChatEvent) {
        val message = TextMessageContent("[${ev.player.name}] ${ev.message}")
        sendMessage(message)
    }
}
