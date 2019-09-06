package net.rrerr.minetrix

import de.msrd0.matrix.client.event.FormattedTextMessageContent
import de.msrd0.matrix.client.event.MessageContent
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent

class MessageFormatter(var message: MessageContent) {
    val URL_REGEX = Regex("\\b(https?://)?.+\\..+(/|/\\S)?\\b")

    fun isFormatteed() =
        message is FormattedTextMessageContent

    fun createLink() {

    }

    fun parse() {

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
        return ArrayList()
    }
}