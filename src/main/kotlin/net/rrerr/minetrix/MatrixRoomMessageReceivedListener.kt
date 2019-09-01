package net.rrerr.minetrix

import de.msrd0.matrix.client.event.FileMessageContent
import de.msrd0.matrix.client.event.ImageMessageContent
import de.msrd0.matrix.client.event.MessageTypes
import de.msrd0.matrix.client.event.UrlMessageContent
import de.msrd0.matrix.client.listener.RoomMessageReceivedEvent
import de.msrd0.matrix.client.listener.RoomMessageReceivedListener
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.io.FileUtils

class MatrixRoomMessageReceivedListener(private val plugin : Main) : RoomMessageReceivedListener {
    override fun call(event: RoomMessageReceivedEvent): Boolean {
        if (event.room.toString() != plugin.room!!.toString()) {
            return true
        }

        if (event.msg.sender == plugin.matrixClient!!.id) {
            return true
        }

        if (event.msg.content.msgtype == MessageTypes.IMAGE) {
            val messageContent = event.msg.content as ImageMessageContent
            val imageType = messageContent.mimetype.split('/').last().toUpperCase()
            val url = getMediaUrl(plugin, messageContent)
            sendLinkMessage(url, "Image [${messageContent.width}x${messageContent.height}, $imageType]")
            return true
        }

        if (event.msg.content.msgtype == MessageTypes.FILE) {
            val messageContent = event.msg.content as FileMessageContent
            val url = getMediaUrl(plugin, messageContent)
            val size = FileUtils.byteCountToDisplaySize(messageContent.size!!.toLong())
            sendLinkMessage(url, "File [${messageContent.filename}, $size]")
            return true
        }

        plugin.server.broadcastMessage("[§2${event.msg.sender}§r] ${event.msg.body}")
        return true
    }

    private fun sendLinkMessage(url: String, label: String) {
        val chatLink = TextComponent(label)
        chatLink.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        plugin.server.onlinePlayers.forEach { it.spigot().sendMessage(chatLink) }
    }

    private fun getMediaUrl(plugin: Main, message: UrlMessageContent): String {
        return "${plugin.matrixClient!!.hs.base}media/v1/download${message.url!!.mediaId}"
    }
}