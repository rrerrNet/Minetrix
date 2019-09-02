package net.rrerr.minetrix

import de.msrd0.matrix.client.event.*
import de.msrd0.matrix.client.listener.RoomMessageReceivedEvent
import de.msrd0.matrix.client.listener.RoomMessageReceivedListener
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.*
import org.apache.commons.io.FileUtils

class MatrixRoomMessageReceivedListener(private val plugin: Main) : RoomMessageReceivedListener {
    override fun call(event: RoomMessageReceivedEvent): Boolean {
        if (event.room.toString() != plugin.room!!.toString()) {
            return true
        }

        if (event.msg.sender == plugin.matrixClient!!.id) {
            return true
        }

        val content = event.msg.content
        val message = createMessagePrefix(event.msg)
        val description = getMediaDescription(content)
        if (content.msgtype == MessageTypes.TEXT) {
            message.add(TextComponent(content.body))
        } else {
            message.add(createLink(getMediaUrl(content as UrlMessageContent), description))
        }

        sendMessage(message.toArray(arrayOfNulls<BaseComponent>(message.size)))
        return true
    }

    private fun getMediaDescription(content: MessageContent): String {
        if (content.msgtype == MessageTypes.IMAGE) {
            return getMediaDescription(content as ImageMessageContent)
        } else if (content.msgtype == MessageTypes.FILE) {
            return getMediaDescription(content as FileMessageContent)
        }
        return content.body
    }

    private fun getMediaDescription(content: ImageMessageContent): String {
        val imageType = content.mimetype.split('/').last().toUpperCase()
        return "Image [${content.width}x${content.height}, $imageType]"
    }

    private fun getMediaDescription(content: FileMessageContent): String {
        val size = FileUtils.byteCountToDisplaySize(content.size!!.toLong())
        return "File [${content.filename}, $size]"
    }

    private fun createLink(url: String, label: String): TextComponent {
        val chatLink = TextComponent(label)
        chatLink.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        return chatLink
    }

    private fun sendMessage(message: Array<BaseComponent>) {
        plugin.server.onlinePlayers.forEach { it.spigot().sendMessage(ChatMessageType.CHAT, *message) }
    }

    private fun createMessagePrefix(message: Message): ArrayList<BaseComponent> {
        val value = ArrayList<BaseComponent>()
        val sender = TextComponent(message.sender.toString())
        sender.color = ChatColor.DARK_GREEN
        sender.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "${message.sender}: ")

        if (plugin.useSenderTooltips) {
            val tooltip = ComponentBuilder(message.sender.displayname)
                .append("\nvia Matrix").color(ChatColor.GRAY)
                .create()
            sender.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)
        }

        value.add(TextComponent("["))
        value.add(sender)
        value.add(TextComponent("] "))

        return value
    }

    private fun getMediaUrl(message: UrlMessageContent) =
        "${plugin.matrixClient!!.hs.base}/_matrix/media/v1/download/${message.url!!.toString().replace("mxc://", "")}"
}
