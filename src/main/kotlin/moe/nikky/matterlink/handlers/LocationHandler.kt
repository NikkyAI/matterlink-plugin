package moe.nikky.matterlink.handlers

import moe.nikky.matterlink.MessageHandler
import moe.nikky.matterlink.api.ApiMessage
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.logger
import moe.nikky.matterlink.stripColorOut
import java.util.UUID

object LocationHandler {

    // TODO: rename and move to MessageHandler/Inst
    suspend fun sendToLocations(
        user: String = cfg.outgoing.systemUser,
        msg: String,
//        x: Int = -1, y: Int = -1, z: Int = -1,
//        dimension: Int? = null,
        event: ChatEvent,
        systemuser: Boolean = false,
        uuid: UUID? = null,
        cause: String
    ): Boolean {
        val filter = cfg.outgoing.filter

        val matchesEvent = when (event) {
            ChatEvent.PLAIN -> filter.plain
            ChatEvent.ACTION -> filter.action
            ChatEvent.DEATH -> filter.death
            ChatEvent.JOIN -> filter.join
            ChatEvent.LEAVE -> filter.leave
            ChatEvent.ADVANCEMENT -> filter.advancement
            ChatEvent.BROADCAST -> filter.broadcast
            ChatEvent.STATUS -> filter.status
        }

        if(!matchesEvent) {
            logger.fine("dropped message '$msg' from user: '$user', event not enabled")
            logger.fine("event: $event")
            logger.fine("filter: $filter")
            return false
        }

        val eventStr = when (event) {
            ChatEvent.PLAIN -> ""
            ChatEvent.ACTION -> ApiMessage.USER_ACTION
            ChatEvent.DEATH -> ""
            ChatEvent.JOIN -> ApiMessage.JOIN_LEAVE
            ChatEvent.LEAVE -> ApiMessage.JOIN_LEAVE
            ChatEvent.ADVANCEMENT -> ""
            ChatEvent.BROADCAST -> ""
            ChatEvent.STATUS -> ""
        }

        val username = when {
            systemuser -> cfg.outgoing.systemUser
            else -> user
        }

        val avatar = when {
            systemuser ->
                cfg.outgoing.avatar.systemUserAvatar
            cfg.outgoing.avatar.enable && uuid != null ->
                cfg.outgoing.avatar.urlTemplate.replace("{uuid}", uuid.toString())
            else ->
                null
        }
        when {
            msg.isNotBlank() -> MessageHandler.transmit(
                ApiMessage(
                    username = username.stripColorOut,
                    text = msg.stripColorOut,
                    event = eventStr,
                    gateway = cfg.connect.gateway
                ).apply {
                    avatar?.let {
                        this.avatar = it
                    }
                },
                cause = cause
            )
            else -> logger.warning("WARN: dropped blank message by '$user'")
        }
        logger.fine("sent message, cause: $cause")
        return true
    }
}