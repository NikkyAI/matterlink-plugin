package moe.nikky.matterlink

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin

class MessageInterceptingPlayer(val wrappedSender: Player) : MessageInterceptingSender, Player by wrappedSender {
    private val msgLog = StringBuilder()
    private val spigot = Spigot()

    private inner class Spigot : Player.Spigot() {
        /**
         * Sends this sender a chat component.
         *
         * @param component the components to send
         */
        override fun sendMessage(component: BaseComponent) {
            msgLog.append(BaseComponent.toLegacyText(component)).append('\n')
            wrappedSender.sendMessage()
        }

        /**
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         */
        override fun sendMessage(vararg components: BaseComponent) {
            msgLog.append(BaseComponent.toLegacyText(*components)).append('\n')
            wrappedSender.sendMessage(*components)
        }
    }

    override val messageLog: String
        get() = msgLog.toString()

    override val messageLogStripColor: String?
        get() = ChatColor.stripColor(msgLog.toString())

    override fun clearMessageLog() {
        msgLog.setLength(0)
    }

    override fun sendMessage(message: String) {
        wrappedSender.sendMessage(message)
        msgLog.append(message).append('\n')
    }

    override fun sendMessage(messages: Array<String>) {
        wrappedSender.sendMessage(messages)
        for (message in messages) {
            msgLog.append(message).append('\n')
        }
    }

    override fun spigot(): Player.Spigot {
        return spigot
    }
}