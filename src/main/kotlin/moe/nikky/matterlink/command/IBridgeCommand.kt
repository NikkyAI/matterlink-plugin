package moe.nikky.matterlink.command

import moe.nikky.matterlink.Matterlink
import moe.nikky.matterlink.MessageHandler
import moe.nikky.matterlink.api.ApiMessage
import moe.nikky.matterlink.config.PermissionConfig
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.handlers.TickHandler
import moe.nikky.matterlink.logger
import moe.nikky.matterlink.stripColorOut
import java.util.UUID

abstract class IBridgeCommand {
    abstract val help: String
    abstract val permLevel: Double
    open val timeout: Int = 20

    sealed class CommandEnvironment {
        abstract val uuid: UUID?
        abstract val username: String?

        data class BridgeEnv(
            val name: String,
            val userId: String,
            val platform: String,
            val gateway: String,
            override val uuid: UUID?
        ) : CommandEnvironment() {
            override val username: String?
                get() = uuid?.let { Matterlink.uuidToName(uuid) }
        }

        data class GameEnv(
            override val username: String,
            override val uuid: UUID
        ) : CommandEnvironment()

        suspend fun respond(text: String, cause: String = "") {
            when (this) {
                is BridgeEnv -> {
                    MessageHandler.transmit(
                        ApiMessage(
                            gateway = this.gateway,
                            text = text.stripColorOut
                        ),
                        cause = cause
                    )
                }
                is GameEnv -> {
                    Matterlink.wrappedSendToPlayer(uuid, text)
                }
            }

        }
    }


    private var lastUsed: Int = 0

    val alias: String
        get() = BridgeCommandRegistry.getName(this)!!

    fun reachedTimeout(): Boolean {
        return (TickHandler.tickCounter - lastUsed > timeout)
    }

    fun preExecute() {
        lastUsed = TickHandler.tickCounter
    }

    /**
     *
     * @return consume message flag
     */
    abstract suspend fun execute(alias: String, user: String, env: CommandEnvironment, args: String): Boolean

    fun canExecute(uuid: UUID?): Boolean {
        logger.finer("canExecute this: $this  uuid: $uuid permLevel: $permLevel")
        val canExec = getPermLevel(uuid) >= permLevel
        logger.finer("canExecute return $canExec")
        return canExec
    }

    open fun validate() = true

    companion object {
        fun getPermLevel(uuid: UUID?): Double {
            if (uuid == null) return cfg.command.defaultPermUnauthenticated
            return PermissionConfig.perms[uuid.toString()] ?: cfg.command.defaultPermAuthenticated
        }
    }
}