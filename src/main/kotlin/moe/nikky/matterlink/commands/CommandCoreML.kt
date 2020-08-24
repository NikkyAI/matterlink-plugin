package moe.nikky.matterlink.commands

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.nikky.matterlink.MessageHandler
import moe.nikky.matterlink.command.BridgeCommandRegistry
import moe.nikky.matterlink.config.PermissionConfig
import moe.nikky.matterlink.config.baseCfg
import moe.nikky.matterlink.config.cfg

object CommandCoreML {
    val name = "matterlink"

    val aliases = listOf("ml")

    val usage = "matterlink <connect|disconnect|reload|permAccept>"

    suspend fun execute(args: Array<String>, user: String, uuid: String?): String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "connect" -> {
                GlobalScope.launch {
                    MessageHandler.start("Bridge connected by console", true)
                }
                "Attempting bridge connection!"
            }
            "disconnect" -> {
                MessageHandler.stop("Bridge disconnected by console")
                "Bridge disconnected!"
            }
            "reload" -> {
//                if (MessageHandlerInst.connected)
                MessageHandler.stop("Bridge restarting (reload command issued by console)")
                cfg = baseCfg.load()
                BridgeCommandRegistry.reloadCommands()
//                if (!MessageHandlerInst.connected)
                GlobalScope.launch {
                    MessageHandler.start("Bridge reconnected", false)
                }
                "Bridge config reloaded!"
            }
            "permaccept" -> {
                val requestId = args.getOrNull(1)?.toLowerCase() ?: run {
                    return "no requestId passed"
                }
                val request = PermissionConfig.permissionRequests.getIfPresent(requestId.toLowerCase()) ?: run {
                    return "No request available"
                }
                val nonce = args.getOrNull(2)?.toUpperCase() ?: run {
                    return "no code passed"
                }
                if (request.nonce != nonce) {
                    return "nonce in request does not match with $nonce"
                }
                val powerLevelArg = args.getOrNull(3)?.toDoubleOrNull()
                val powerLevel = powerLevelArg ?: run { return "permLevel cannot be parsed: ${args.getOrNull(3)}" }
                ?: request.powerlevel
                ?: return "no permLevel provided"
                PermissionConfig.add(request.uuid, powerLevel, "${request.user} Authorized by $user")
                PermissionConfig.permissionRequests.invalidate(requestId)
                "added ${request.user} (uuid: ${request.uuid}) with power level: $powerLevel"
            }
            else -> {
                "Invalid arguments for command! \n" +
                        "usage: ${CommandCoreAuth.usage}"
            }
        }
    }

}