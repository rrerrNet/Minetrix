package net.rrerr.minetrix

import de.msrd0.matrix.client.listener.RoomMessageReceivedEvent
import de.msrd0.matrix.client.listener.RoomMessageReceivedListener

class MatrixRoomMessageReceivedListener(private val plugin : Main) : RoomMessageReceivedListener {
    override fun call(event: RoomMessageReceivedEvent): Boolean {
        if (event.room.toString() != plugin.room!!.toString()) {
            return true
        }

        if (event.msg.sender == plugin.matrixClient!!.id) {
            return true
        }

        plugin.server.broadcastMessage("[§2${event.msg.sender}§r] ${event.msg.body}")
        return true
    }
}