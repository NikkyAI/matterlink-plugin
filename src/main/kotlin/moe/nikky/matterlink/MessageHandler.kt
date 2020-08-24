package moe.nikky.matterlink

import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.api.ApiMessage
import moe.nikky.matterlink.api.MessageHandlerBase
import moe.nikky.matterlink.config.cfg

object MessageHandler : MessageHandlerBase() {
    override suspend fun transmit(msg: ApiMessage) {
        transmit(msg, cause = "")
    }

//    override suspend fun sendStatusUpdate(message: String) {
//        LocationHandler.sendToLocations(
//            msg = message,
//            x = 0, y = 0, z = 0, dimension = null,
//            systemuser = true,
//            event = ChatEvent.STATUS,
//            cause = "status update message"
//        )
//    }

    suspend fun transmit(msg: ApiMessage, cause: String, maxLines: Int = cfg.outgoing.inlineLimit) {
        if (msg.username.isEmpty()) {
            msg.username = cfg.outgoing.systemUser

            if (msg.avatar.isEmpty() && cfg.outgoing.avatar.enable) {
                msg.avatar = cfg.outgoing.avatar.systemUserAvatar
            }
        }
        if (msg.gateway.isEmpty()) {
            logger.severe("missing gateway on message: $msg")
            msg.gateway = config.gateway
//            logger.error("dropped message '$msg' due to missing gateway")
//            return
        }

        if (msg.text.lines().count() >= maxLines) {
            try {
                val response = PasteUtil.paste(
                    Paste(
                        description = cause,
                        sections = listOf(
                            PasteSection(
                                name = "log.txt",
                                syntax = "text",
                                contents = msg.text
                            )
                        )
                    )
                )
                msg.text = msg.text.substringBefore('\n')
                    .take(25) + "...  " + response.link
            } catch (e: Exception) {
                logger.severe(cause)
                logger.severe(e.stackTraceString)
            }
        }
        super.transmit(msg)
    }


    fun afterServerSetup() {
        if (cfg.outgoing.announceReady) {
            runBlocking {
                transmit(ApiMessage(text = cfg.outgoing.announceReadyMessage))
            }
        }
    }
}

fun ApiMessage.format(fmt: String): String {
    return fmt.mapFormat(
        mapOf(
            "{username}" to username,
            "{text}" to text,
            "{gateway}" to gateway,
            "{channel}" to channel,
            "{protocol}" to protocol,
            "{username:antiping}" to username.antiping
        )
    )
}