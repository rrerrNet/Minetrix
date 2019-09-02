package net.rrerr.minetrix

import de.msrd0.matrix.client.MatrixAnswerException
import de.msrd0.matrix.client.MatrixClient
import de.msrd0.matrix.client.NoTokenException
import de.msrd0.matrix.client.event.MessageContent
import org.bukkit.event.Listener
import java.util.*

abstract class ListenerBase(protected val plugin: Main) : Listener {
    /**
     * reimpl of de.msrd0.matrix.client.Room.sendMessage to use UUIDs instead of a serial id.
     * This is because serial ids will cause messages not to be appear or be rejected by the homeserver
     */
    @Throws(MatrixAnswerException::class)
    protected fun sendMessage(msg : MessageContent) {
        val id = plugin.room!!.id
        val res = plugin.target!!.put("_matrix/client/r0/rooms/$id/send/m.room.message/${UUID.randomUUID()}",
            plugin.matrixClient!!.token ?: throw NoTokenException(), plugin.matrixClient!!.id, msg.json)
        MatrixClient.checkForError(res)
    }
}
