package moe.nikky.matterlink.handlers

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.api.ApiMessage
import moe.nikky.matterlink.command.BridgeCommandRegistry
import moe.nikky.matterlink.logger
import java.util.UUID

object ChatProcessor: Runnable {
    private val channel = Channel<Task>(100)

    override fun run() = runBlocking {
        var next: Task? = channel.poll()
        while(next != null) {
            processTask(
                    user = next.user,
                    msg = next.msg,
                    event = next.event,
                    uuid = next.uuid
            )
            next = channel.poll()
        }
    }

    suspend fun sendToBridge(
            user: String,
            msg: String,
            event: ChatEvent,
            uuid: UUID? = null
    ) {
        channel.send(
                Task(
                        user = user,
                        msg = msg,
                        event = event,
                        uuid = uuid
                )
        )
    }

    private suspend fun processTask(
            user: String,
            msg: String,
            event: ChatEvent,
            uuid: UUID? = null
    ) {
        val message = msg.trim()

        if (uuid != null && BridgeCommandRegistry.handleCommand(message, user, uuid)) {
            return
        }
        when {
            message.isNotBlank() -> LocationHandler.sendToLocations(
                    user = user,
                    msg = message,
                    event = event,
                    cause = "Message from $user",
                    uuid = uuid
            )
            else -> logger.warning("WARN: dropped blank message by '$user'")
        }
    }

    private data class Task(
        val user: String,
        val msg: String,
        val event: ChatEvent,
        val uuid: UUID? = null
    )
}
