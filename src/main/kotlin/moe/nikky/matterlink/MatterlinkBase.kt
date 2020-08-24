package moe.nikky.matterlink

import moe.nikky.matterlink.command.IBridgeCommand
import moe.nikky.matterlink.command.IMinecraftCommandSender
import java.util.*

interface MatterlinkBase {
    abstract fun commandSenderFor(
            user: String,
            env: IBridgeCommand.CommandEnvironment,
            op: Boolean
    ): IMinecraftCommandSender

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedSendToPlayer(username: String, msg: String)
    abstract fun wrappedSendToPlayer(uuid: UUID, msg: String)
    abstract fun isOnline(username: String): Boolean
    abstract fun nameToUUID(username: String): UUID?
    abstract fun uuidToName(uuid: UUID): String?
}