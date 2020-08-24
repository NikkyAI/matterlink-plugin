package moe.nikky.matterlink

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.command.BridgeCommandRegistry
import moe.nikky.matterlink.command.IBridgeCommand
import moe.nikky.matterlink.command.IMinecraftCommandSender
import moe.nikky.matterlink.commands.CommandCoreAuth
import moe.nikky.matterlink.commands.CommandCoreML
import moe.nikky.matterlink.config.BaseConfig
import moe.nikky.matterlink.config.IdentitiesConfig
import moe.nikky.matterlink.config.PermissionConfig
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.handlers.ChatEvent
import moe.nikky.matterlink.handlers.ChatProcessor
import moe.nikky.matterlink.handlers.ServerChatHandler
import moe.nikky.matterlink.handlers.TickHandler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*


class Matterlink : JavaPlugin(), MatterlinkBase {
    companion object : MatterlinkBase {
        private lateinit var instance: Matterlink
        val modVersion: String = "0.0.1"
        /**
         * in milliseconds
         */
        var serverStartTime: Long = 0
        override fun commandSenderFor(user: String, env: IBridgeCommand.CommandEnvironment, op: Boolean): IMinecraftCommandSender =
                instance.commandSenderFor(user, env, op)

        override fun wrappedSendToPlayers(msg: String) =
                instance.wrappedSendToPlayers(msg)

        override fun wrappedSendToPlayer(username: String, msg: String) =
                instance.wrappedSendToPlayer(username, msg)

        override fun wrappedSendToPlayer(uuid: UUID, msg: String) =
                instance.wrappedSendToPlayer(uuid, msg)

        override fun isOnline(username: String): Boolean =
                instance.isOnline(username)

        override fun nameToUUID(username: String): UUID? =
                instance.nameToUUID(username)

        override fun uuidToName(uuid: UUID): String? =
                instance.uuidToName(uuid)

        fun getUptimeAsString(): String {
            val total = (System.currentTimeMillis() - serverStartTime) / 1000
            val s = total % 60
            val m = (total / 60) % 60
            val h = (total / 3600) % 24
            val d = total / 86400

            fun timeFormat(unit: Long, name: String) = when {
                unit > 1L -> "$unit ${name}s "
                unit == 1L -> "$unit $name "
                else -> ""
            }

            var result = ""
            result += timeFormat(d, "Day")
            result += timeFormat(h, "Hour")
            result += timeFormat(m, "Minute")
            result += timeFormat(s, "Second")
            return result
        }
    }
    init {
        instance = this
    }

    override fun onLoad() {
        super.onLoad()

        logger.info { "onLoad" }
    }

    override fun onDisable() {
        super.onDisable()

        onShutdown()
    }

    override fun onEnable() {
        serverStartTime = System.currentTimeMillis()
        moe.nikky.matterlink.logger = this.logger

        val tickTask = this.server.scheduler.runTaskTimer(
                this,
                TickHandler,
                0,
                1
        )

        val rootFolder = File(".").absoluteFile
        val configFolder = rootFolder.resolve("config")
        configFolder.mkdirs()

        logger.info { "Building bridge!" }

        cfg = BaseConfig(configFolder).load()

        logger.info { "HELLO WORLD" }


//        val sender = MessageInterceptingConsoleSender(server.consoleSender)
//        val commandline = "minecraft:list"
//        val status =  server.dispatchCommand(sender, commandline)
//        logger.info("$commandline: $status")
//        logger.info("response")
//        println(sender.messageLog)

        getCommand("matterlink")?.apply {
            setExecutor { sender: CommandSender, command: Command, label: String, args: Array<String> ->
                runBlocking {
                    CommandCoreML.execute(args, sender.name, (sender as? Player)?.uniqueId?.toString())
                }
                true
            }
            val argOptions = listOf("connect", "disconnect", "reload", "permaccept")
            setTabCompleter { sender, command, alias, args ->
                logger.info ("autocompleting: ${command.name}, [${args.joinToString(",")}]")
                // TODO: when permAccept -> list codes -> nonce

                when {
                    !sender.isOp -> listOf()
                    args.isEmpty() -> argOptions
                    args.size == 1 -> when(val first = args.first()) {
                        "connect" -> listOf()
                        "disconnect" -> listOf()
                        "reload" -> listOf()
                        "permaccept" -> PermissionConfig.permissionRequests.asMap().keys.toList()
                        else -> argOptions.filter { it.startsWith(args.first()) }
                    }
                    args.size == 2 && args.first() == "permaccept" -> when(val second = args[1]) {
                        // TODO: list codes
                        else ->  PermissionConfig.permissionRequests.asMap().keys.toList().filter { it.startsWith(second) }
                    }
                    args.size == 3 && args.first() == "permaccept" -> when(val third = args[2]) {
                        // TODO: list codes
                        else -> (sender as? Player)?.uniqueId?.toString()?.let {
                            PermissionConfig.perms[it] ?: 50
                        }?.let {
                            listOf(it.toString())
                        } ?: listOf("50")
                    }
                    else -> listOf()
                }
            }
        }

        getCommand("authenticate")?.apply {
            setExecutor { sender: CommandSender, command: Command, label: String, args: Array<String> ->
                CommandCoreAuth.execute(args, sender.name, (sender as? Player)?.uniqueId?.toString())
                true
            }
            val argOptions = listOf("accept", "reject")
            setTabCompleter { sender, command, alias, args ->
                logger.info ("autocompleting: ${command.name}, [${args.joinToString(",")}]")
                when {
                    !sender.isOp -> listOf()
                    args.isEmpty() -> argOptions
                    args.size == 1 -> when(val first = args.first()) {
                        "accept" ->IdentitiesConfig.authRequests.asMap().keys.toList()
                        "reject" -> IdentitiesConfig.authRequests.asMap().keys.toList()
                        else -> argOptions.filter { it.startsWith(args.first()) }
                    }
//                    else -> IdentitiesConfig.authRequests.asMap().keys.toList()
                    args.first().let { it == "accept" || it == "reject" } && args.size == 2 -> when(val second = args[1]) {
                        else -> IdentitiesConfig.authRequests.asMap().keys.toList().filter { it.startsWith(second) }
                    }
                    else -> {
                        listOf()
                    }
                }
            }
        }

        getCommand("say")?.let { c ->
            logger.info("command: $c")
            c.setExecutor { sender: CommandSender, command: Command, label: String, args: Array<String> ->
                val vanillaCommand = "minecraft:say ${args.joinToString(" ")}"
                logger.info("dispatching: $vanillaCommand")
                server.dispatchCommand(sender, vanillaCommand);

                runBlocking {
                    ChatProcessor.sendToBridge(sender.name, args.joinToString(" "), ChatEvent.BROADCAST, uuid = (sender as? Player)?.uniqueId)
                }
                true
            }
        }
        println("registering listener")

        Bukkit.getPluginManager().registerEvents(MatterlinkListener(), this)

        logger.fine("Registering bridge commands")
        registerBridgeCommands()

        runBlocking {
            start()
        }
        val receiveIncomingTask = server.scheduler.runTaskTimer(
                this,
                ServerChatHandler,
                0,
                5
        )
        val processChat = server.scheduler.runTaskTimer(
                this,
                ChatProcessor,
                0,
                5
        )
        logger.info("bridge started")
    }

//    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
//        return super.onTabComplete(sender, command, alias, args)?.toMutableList().also { list ->
//
//            list
//
//        }
//    }

    override fun commandSenderFor(
            user: String,
            env: IBridgeCommand.CommandEnvironment,
            op: Boolean
    ): IMinecraftCommandSender {
        return object: IMinecraftCommandSender(user, env, op) {
            override fun execute(alias: String, cmdString: String): Boolean {
                if(!canExecute(alias)) return false
                val interceptingSender: MessageInterceptingSender = /*server.getPlayer(user)?.let {
                    MessageInterceptingPlayer(it)
                } ?:*/ if(op) {
                    MessageInterceptingConsoleSender(server.consoleSender)
                } else {
                    return false
                }
//                val interceptingSender = MessageInterceptingConsoleSender(server.consoleSender)
                logger.info ("executing $cmdString")
                val status =  server.dispatchCommand(interceptingSender, cmdString)
                runBlocking {
                    logger.info ("response: $status ${interceptingSender.messageLogStripColor}")
                    appendReply(interceptingSender.messageLogStripColor ?: "")
                    sendReply(cmdString)
                }
                return status
            }
        }
    }

    override fun wrappedSendToPlayers(msg: String) {
        logger.fine("broadcasting: $msg")
//        server.playerManager.playerList.forEach {
//            it.sendChatMessage(msg)
//        }
        Bukkit.broadcastMessage(msg)
    }

    override fun wrappedSendToPlayer(username: String, msg: String) {
        server.getPlayer(username)?.sendMessage(msg)
    }

    override fun wrappedSendToPlayer(uuid: UUID, msg: String) {
        server.getPlayer(uuid)?.sendMessage(msg)
    }

    override fun isOnline(username: String): Boolean {
        return server.getPlayer(username)?.isOnline == true
    }

    override fun nameToUUID(username: String): UUID? {
        return server.getPlayer(username)?.uniqueId
    }

    override fun uuidToName(uuid: UUID): String? {
        return server.getPlayer(uuid)?.displayName
    }



    fun registerBridgeCommands() {
        BridgeCommandRegistry.reloadCommands()
    }

    suspend fun start() {
        serverStartTime = System.currentTimeMillis()

        if (cfg.connect.autoConnect) {
            // TODO: keep job handle around for kill
            GlobalScope.launch {
                MessageHandler.start(cfg.outgoing.announceConnectMessage, true)
            }
        }
//        UpdateChecker.check()
    }

    fun onShutdown() = runBlocking {
        stop()
    }

    suspend fun stop() {
        MessageHandler.stop(cfg.outgoing.announceDisconnectMessage)
    }
}