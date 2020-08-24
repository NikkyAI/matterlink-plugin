package moe.nikky.matterlink.handlers

import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.antiping
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.mapFormat
import moe.nikky.matterlink.stripColorOut


object JoinLeaveHandler {
    suspend fun handleJoin(
            player: String,
            joinMessage: String
    ) {
        if (cfg.outgoing.filter.join) {
            val msg = cfg.outgoing.joinPart.joinServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping,
                            "{join_msg}" to joinMessage
                    )
            )
            LocationHandler.sendToLocations(
                    msg = msg,
                    event = ChatEvent.JOIN,
                    systemuser = true,
                    cause = "$player joined"
            )
        }
    }

    @JvmName("handleLeave")
    fun handleLeaveJava(player: String, reason: String) = runBlocking {
        handleLeave(player, reason)
    }

    suspend fun handleLeave(
            player: String,
            quitMessage: String
    ) {
        if (cfg.outgoing.filter.leave) {
            val msg = cfg.outgoing.joinPart.partServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping,
                            "{quit_msg}" to quitMessage
                    )
            )
            LocationHandler.sendToLocations(
                    msg = msg,
                    event = ChatEvent.JOIN,
                    systemuser = true,
                    cause = "$player left"
            )
        }
    }
}