package moe.nikky.matterlink.handlers

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.api.ApiMessage
import moe.nikky.matterlink.command.BridgeCommandRegistry
import moe.nikky.matterlink.Matterlink
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.format
import moe.nikky.matterlink.jsonNonstrict
import moe.nikky.matterlink.logger

object ServerChatHandler : Runnable {
    private val channel = Channel<ApiMessage>(100)
    override fun run() = runBlocking {
        var next: ApiMessage? = channel.poll()
        while(next != null) {
            logger.info("processing next: $next")
            processApiMessage(next)
            next = channel.poll()
        }
    }


    suspend fun writeMessageToChat(nextMessage: ApiMessage) {
        logger.info("sending to channel: $nextMessage")
        channel.send(nextMessage)
    }

    private suspend fun processApiMessage(nextMessage: ApiMessage) {

        val filter = cfg.incoming.filter

        val sourceGateway = nextMessage.gateway

        if(sourceGateway != cfg.connect.gateway) {
            logger.fine("message from mismatching gateway: '$sourceGateway' dropped")
            return
        }

        logger.info("processApiMessage: $nextMessage")
        if (nextMessage.event.isEmpty()) {
            if (filter.commands) {
                // try handle command
                if (BridgeCommandRegistry.handleCommand(nextMessage)) return
            }
        }

        val matchesEvent = when (nextMessage.event) {
            "" -> filter.plain
            ApiMessage.JOIN_LEAVE -> filter.join_leave
            ApiMessage.USER_ACTION -> filter.action
            else -> {
                logger.severe("unknown event type '${nextMessage.event}' on incoming message")
                return
            }
        }

        if(!matchesEvent) {
            logger.fine("dropped message '${nextMessage.text}' from user: '${nextMessage.username}', event not enabled")
            logger.fine("event: ${nextMessage.event}")
            logger.fine("filter: $filter")
            return
        }

        val message = when (nextMessage.event) {
            "" -> {
                nextMessage.format(cfg.incoming.chat)
            }
            ApiMessage.USER_ACTION -> nextMessage.format(cfg.incoming.action)
            ApiMessage.JOIN_LEAVE -> nextMessage.format(cfg.incoming.joinPart)
            else -> {
                val user = nextMessage.username
                val text = nextMessage.text
                val json = jsonNonstrict.stringify(ApiMessage.serializer(), nextMessage)
                logger.fine("Threw out message with unhandled event: ${nextMessage.event}")
                logger.fine(" Message contents:")
                logger.fine(" User: $user")
                logger.fine(" Text: $text")
                logger.fine(" JSON: $json")
                return
            }
        }

        Matterlink.wrappedSendToPlayers(message)
    }
}
