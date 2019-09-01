package net.rrerr.minetrix

import de.msrd0.matrix.client.MatrixAnswerException
import de.msrd0.matrix.client.MatrixClient.Companion.checkForError
import de.msrd0.matrix.client.NoTokenException
import de.msrd0.matrix.client.event.MessageContent
import de.msrd0.matrix.client.event.TextMessageContent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

class MinecraftChatMessageListener(private val plugin: Main) : Listener {
    @EventHandler
    fun messageReceived(ev: AsyncPlayerChatEvent) {
        val message = TextMessageContent("[${ev.player.name}] ${ev.message}")
        sendMessage(message)
    }

    /**
     * reimpl of de.msrd0.matrix.client.Room.sendMessage to use UUIDs instead of a serial id.
     * This is because serial ids will cause messages not to be appear or be rejected by the homeserver
     */
    @Throws(MatrixAnswerException::class)
    private fun sendMessage(msg : MessageContent) {
        val id = plugin.room!!.id
        val res = plugin.target!!.put("_matrix/client/r0/rooms/$id/send/m.room.message/${UUID.randomUUID()}",
            plugin.matrixClient!!.token ?: throw NoTokenException(), plugin.matrixClient!!.id, msg.json)
        checkForError(res)
    }
}