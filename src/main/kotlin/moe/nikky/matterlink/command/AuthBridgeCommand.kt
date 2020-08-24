package moe.nikky.matterlink.command

import moe.nikky.matterlink.Matterlink
import moe.nikky.matterlink.config.AuthRequest
import moe.nikky.matterlink.config.IdentitiesConfig
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.randomString
import java.util.UUID

object AuthBridgeCommand : IBridgeCommand() {
    val syntax = "Syntax: auth [username]"
    override val help: String = "Requests authentication on the bridge. $syntax"
    override val permLevel: Double
        get() = cfg.command.defaultPermUnauthenticated

    override suspend fun execute(alias: String, user: String, env: CommandEnvironment, args: String): Boolean {
        if (env !is CommandEnvironment.BridgeEnv) {
            env.respond("please initiate authentication from linked external chat")
            return true
        }

        val uuid = env.uuid
        if (uuid != null) {
            val name = Matterlink.uuidToName(uuid)
            env.respond("you are already authenticated as name: $name uuid: $uuid")
            return true
        }

        val argList = args.split(' ', limit = 2)
        val target = argList.getOrNull(0) ?: run {
            env.respond(
                "no username/uuid provided\n" +
                        syntax
            )

            return true
        }

        var targetUserName = target

        val targetUUid: String = Matterlink.nameToUUID(target)?.toString() ?: run {
            try {
                targetUserName = Matterlink.uuidToName(UUID.fromString(target)) ?: run {
                    env.respond("cannot find player by username/uuid $target")
                    return true
                }
            } catch (e: IllegalArgumentException) {
                env.respond("invalid username/uuid $target")
                return true
            }
            target
        }

        val online = Matterlink.isOnline(targetUserName)
        if (!online) {
            env.respond("$targetUserName is not online, please log in and try again to send instructions")
            return true
        }
        val nonce = randomString(length = 3).toUpperCase()

        val requestId = user.toLowerCase()
        Matterlink.wrappedSendToPlayer(targetUserName, "have you requested authentication with the MatterLink system?")
        Matterlink.wrappedSendToPlayer(targetUserName, "if yes please execute /auth accept $user $nonce")
        Matterlink.wrappedSendToPlayer(targetUserName, "otherwise you may ignore this message")


        IdentitiesConfig.authRequests.put(
            requestId,
            AuthRequest(
                username = targetUserName,
                uuid = targetUUid,
                nonce = nonce,
                platform = env.platform,
                userid = env.userId
            )
        )
        env.respond("please accept the authentication request ingame, do not share the code")

        return true
    }
}