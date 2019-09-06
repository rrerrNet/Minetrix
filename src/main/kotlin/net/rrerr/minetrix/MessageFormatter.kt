package net.rrerr.minetrix

import de.msrd0.matrix.client.event.FormattedTextMessageContent
import de.msrd0.matrix.client.event.MessageContent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory


class MessageFormatter(var message: MessageContent) {
    companion object {
        val TAGS = mapOf(
            "B" to ChatColor.BOLD,
            "STRONG" to ChatColor.BOLD,
            "EM" to ChatColor.ITALIC,
            "I" to ChatColor.ITALIC,
            "U" to ChatColor.UNDERLINE,
            "DEL" to ChatColor.STRIKETHROUGH
        )
    }

    val URL_REGEX = Regex("\\b(https?://)?.+\\..+(/|/\\S)?\\b")
    val currentFormat = mutableSetOf<ChatColor>()

    fun isFormatteed() =
        message is FormattedTextMessageContent

    fun parse(): Document {
        val body = (message as FormattedTextMessageContent).formattedBody
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val input = InputSource()
        input.characterStream = StringReader("<p>$body</p>")
        return builder.parse(input)
    }

    fun nodesToComponents(nodes: NodeList): ArrayList<BaseComponent> {
        val list = ArrayList<BaseComponent>()
        for (i in 0..nodes.length-1) {
            val node = nodes.item(i)
            if (!node.hasChildNodes()) {
                val text = node.textContent
                val component = TextComponent(text)
                component.isBold = currentFormat.contains(ChatColor.BOLD)
                component.isItalic = currentFormat.contains(ChatColor.ITALIC)
                component.isUnderlined = currentFormat.contains(ChatColor.UNDERLINE)
                component.isStrikethrough = currentFormat.contains(ChatColor.STRIKETHROUGH)

                if (text.matches(URL_REGEX)) {
                    component.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, text)
                }

                list.add(component)
            } else {
                val tag = node.nodeName.toUpperCase()
                var style: ChatColor? = null
                if (TAGS.containsKey(tag)) {
                    style = TAGS[tag]!!
                    currentFormat.add(style)
                }

                list.addAll(nodesToComponents(node.childNodes))

                if (style !== null) {
                    currentFormat.remove(TAGS[tag]!!)
                }
            }
        }

        return list
    }

    fun formatPlainMessage(): ArrayList<BaseComponent> {
        val words = message.body.split(" ")
        val components = ArrayList<BaseComponent>()
        val nextComponent = ArrayList<String>()
        words.forEach { word ->
            if (word.matches(URL_REGEX)) {
                if (nextComponent.size > 0) {
                    components.add(TextComponent(nextComponent.joinToString(separator = " ")))
                    nextComponent.clear()
                }

                val link = TextComponent(word)
                link.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, word)
                components.add(link)
            } else {
                nextComponent.add(word)
            }
        }

        if (nextComponent.size > 0) {
            components.add(TextComponent(nextComponent.joinToString(separator = " ")))
        }

        return components
    }

    fun format(): ArrayList<BaseComponent> {
        if (isFormatteed()) {
            return nodesToComponents(parse().childNodes)
        }
        return formatPlainMessage()
    }
}