package moe.nikky.matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.api.Marshaller
import blue.endless.jankson.api.SyntaxError
import blue.endless.jankson.impl.MarshallerImpl.getFallback
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl
import moe.nikky.matterlink.MessageHandler
import moe.nikky.matterlink.getOrDefault
import moe.nikky.matterlink.logger
import moe.nikky.matterlink.registerSerializer
import moe.nikky.matterlink.stackTraceString
import java.io.File
import java.io.FileNotFoundException

lateinit var cfg: BaseConfig.MatterLinkConfig
lateinit var baseCfg: BaseConfig

data class BaseConfig(val rootDir: File) {
    val cfgDirectory: File = rootDir.resolve("matterlink")
    val configFile: File = cfgDirectory.resolve("matterlink.hjson")

    init {
        logger.info("Reading bridge blueprints... from $rootDir")
        baseCfg = this
    }

    data class MatterLinkConfig(
            val connect: ConnectOptions = ConnectOptions(),
            val incoming: IncomingOptions = IncomingOptions(),
            val outgoing: OutgoingOptions = OutgoingOptions(),
            val command: CommandOptions = CommandOptions(),
            val update: UpdateOptions = UpdateOptions()
    )

    data class CommandOptions(
        val prefix: Char = '!',
        val enable: Boolean = true,
        val authRequests: Boolean = true,
        val permisionRequests: Boolean = true,
        val defaultPermUnauthenticated: Double = 0.0,
        val defaultPermAuthenticated: Double = 1.0
    )

    data class ConnectOptions(
        val url: String = "http://localhost:4242",
        val gateway: String = "minecraft",
        val authToken: String = "",
        val autoConnect: Boolean = true,
        val reconnectWait: Long = 500
    )

    data class IncomingOptions(
        val chat: String = "<{username}> {text}",
        val joinPart: String = "§6-- {username} {text}",
        val action: String = "§5* {username} {text}",
        val stripColors: Boolean = true,
        val filter: FilterIncoming = FilterIncoming()
    )

    data class FilterIncoming(
        val plain: Boolean = true,
        val action: Boolean = true,
        val join_leave: Boolean = false,
        val commands: Boolean = true
    )

    data class OutgoingOptions(
            val systemUser: String = "Server",
        //outgoing toggles
            val announceConnect: Boolean = true,
            val announceDisconnect: Boolean = true,
            val announceReady: Boolean = true,
            val announceConnectMessage: String = "Server started, connecting to matterbridge API",
            val announceDisconnectMessage: String = "Server shutting down, disconnecting from matterbridge API",
            val announceReadyMessage: String = "World loaded",
            val stripColors: Boolean = true,
            val pasteEEKey: String = "",
            val inlineLimit: Int = 5,
            val filter: FilterOutgoing = FilterOutgoing(),

            val joinPart: JoinPartOptions = JoinPartOptions(),
            var avatar: AvatarOptions = AvatarOptions(),
            val death: DeathOptions = DeathOptions()
    )

    data class FilterOutgoing(
        val plain: Boolean = true,
        val action: Boolean = true,
        val join: Boolean = false,
        val leave: Boolean = false,
        val advancement: Boolean = false,
        val death: Boolean = false,
        val broadcast: Boolean = false,
        val status: Boolean = false
    )

    data class DeathOptions(
        val damageType: Boolean = true,
        val damageTypeMapping: Map<String, Array<String>> = mapOf(
            "CONTACT" to arrayOf("\uD83D\uDC80"), //💀
            "ENTITY_ATTACK" to arrayOf("\uD83D\uDC80"), //💀
            "ENTITY_SWEEP_ATTACK" to arrayOf("⚔", "🗡"),
            "PROJECTILE" to arrayOf("🎯", "🏹", "💘"),
            // "SUFFOCATION" to arrayOf(),
            "FALL" to arrayOf("⯯"),
            "FIRE" to arrayOf("\uD83D\uDD25"),
            "FIRE_TICK" to arrayOf("\uD83D\uDD25"),
            "MELTING" to arrayOf("\uD83D\uDD25"),
            "LAVA" to arrayOf("\uD83D\uDD25"),
            "DROWNING" to arrayOf("\uD83C\uDF0A"), //🌊
            "BLOCK_EXPLOSION" to arrayOf("💥💥💥"),
            "ENTITY_EXPLOSION" to arrayOf("💥💥💥"),
            "VOID" to arrayOf("✴"),
            "LIGHTNING" to arrayOf("🌩", "⚡"),
            "SUICIDE" to arrayOf("\uD83D\uDC7B"), //👻
            "STARVATION" to arrayOf("🚫🥪"),
            "POISON" to arrayOf("☠"),
            "MAGIC" to arrayOf("✨", "⚚"),
            "WITHER" to arrayOf("\uD83D\uDD71"),
            "FALLING_BLOCK" to arrayOf("🌠"),
            "THORNS" to arrayOf("\uD83C\uDF39"),
            "DRAGON_BREATH" to arrayOf("\uD83D\uDC32"), //🐲
            "CUSTOM" to arrayOf("⁉"),
            "FLY_INTO_WALL" to arrayOf("\uD83D\uDCA8"),
            "HOT_FLOOR" to arrayOf("♨️"),
            // "CRAMMING" to arrayOf(),
            "DRYOUT" to arrayOf("♨️"),

            "inFire" to arrayOf("\uD83D\uDD25"), //🔥
            "lightningBolt" to arrayOf("\uD83C\uDF29"), //🌩
            "onFire" to arrayOf("\uD83D\uDD25"), //🔥
            "lava" to arrayOf("\uD83D\uDD25"), //🔥
            "hotFloor" to arrayOf("♨️"),
            "inWall" to arrayOf(),
            "cramming" to arrayOf(),
            "drown" to arrayOf("\uD83C\uDF0A"), //🌊
            "starve" to arrayOf("\uD83D\uDC80"), //💀
            "cactus" to arrayOf("\uD83C\uDF35"), //🌵
            "fall" to arrayOf("\u2BEF️"), //⯯️
            "flyIntoWall" to arrayOf("\uD83D\uDCA8"), //💨
            "outOfWorld" to arrayOf("\u2734"), //✴
            "generic" to arrayOf("\uD83D\uDC7B"), //👻
            "magic" to arrayOf("✨", "⚚"),
            "indirectMagic" to arrayOf("✨", "⚚"),
            "wither" to arrayOf("\uD83D\uDD71"), //🕱
            "anvil" to arrayOf("🌠"),
            "fallingBlock" to arrayOf("🌠"),
            "dragonBreath" to arrayOf("\uD83D\uDC32"), //🐲
            "fireworks" to arrayOf("\uD83C\uDF86"), //🎆

            "mob" to arrayOf("\uD83D\uDC80"), //💀
            "player" to arrayOf("\uD83D\uDDE1"), //🗡
            "arrow" to arrayOf("\uD83C\uDFF9"), //🏹
            "thrown" to arrayOf("彡°"),
            "thorns" to arrayOf("\uD83C\uDF39"), //🌹
            "explosion" to arrayOf("\uD83D\uDCA3", "\uD83D\uDCA5"), //💣 💥
            "explosion.player" to arrayOf("\uD83D\uDCA3", "\uD83D\uDCA5"), //💣 💥
            "ieWireShock" to arrayOf("\uD83D\uDD0C", "\u26A1"), //🔌 ⚡
            "immersiverailroading:hitByTrain" to arrayOf(
                "\uD83D\uDE82",
                "\uD83D\uDE83",
                "\uD83D\uDE84",
                "\uD83D\uDE85",
                "\uD83D\uDE87",
                "\uD83D\uDE88",
                "\uD83D\uDE8A"
            ) //🚂 🚃 🚄 🚅 🚇 🚈 🚊
        )
    )

    data class AvatarOptions(
        val enable: Boolean = true,
        val urlTemplate: String = "https://visage.surgeplay.com/head/512/{uuid}",
        val systemUserAvatar: String = ""
    )

    data class JoinPartOptions(
        val joinServer: String = "{username:antiping} has connected to the server, join message: '{join_msg}'",
        val partServer: String = "{username:antiping} has disconnected from the server, quit message: '{quit_msg}'"
    )

    data class UpdateOptions(
        val enable: Boolean = true
    )

    companion object {
        val jankson = Jankson
            .builder()
            .registerDeserializer(JsonObject::class.java, MatterLinkConfig::class.java) { it, marshaller ->
                with(MatterLinkConfig()) {
                    MatterLinkConfig(
                        command = it.getOrDefault(
                            "command",
                            command,
                            "User commands"
                        ),
                        connect = it.getOrDefault(
                            "connect",
                            connect,
                            "Connection Settings"
                        ),
                        incoming = it.getOrDefault(
                            "incoming",
                            incoming,
                            """
                                         Gateway -> Server
                                         Options all about receiving messages from the API
                                         Formatting options:
                                         Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}
                                         """.trimIndent()
                        ),
                        outgoing = it.getOrDefault(
                            "outgoing",
                            outgoing,
                            """
                                         Server -> Gateway
                                         Options all about sending messages to the API
                                         """.trimIndent()
                        ),
                        update = it.getOrDefault(
                            "update",
                            update,
                            "Update Settings"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, FilterOutgoing::class.java) { it, marshaller ->
                with(FilterOutgoing()) {
                    FilterOutgoing(
                        plain = it.getOrDefault("plain", plain, "transmit join events"),
                        action = it.getOrDefault("action", action, "transmit join events"),
                        join = it.getOrDefault("join", join, "transmit join events"),
                        leave = it.getOrDefault("leave", leave, "transmit leave events"),
                        advancement = it.getOrDefault("advancement", advancement, "transmit advancements"),
                        death = it.getOrDefault("death", death, "transmit death messages"),
                        broadcast = it.getOrDefault("say", broadcast, "transmit broadcasts"),
                        status = it.getOrDefault("status", status, "transmit status updates")
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, FilterIncoming::class.java) { it, marshaller ->
                with(FilterIncoming()) {
                    FilterIncoming(
                        plain = it.getOrDefault("plain", plain, "transmit join events"),
                        action = it.getOrDefault("action", action, "transmit join events"),
                        join_leave = it.getOrDefault("join_leave", join_leave, "transmit join_leave events"),
                        commands = it.getOrDefault("commands", commands,"receive commands")
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, CommandOptions::class.java) { it, marshaller ->
                with(CommandOptions()) {
                    CommandOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "Enable MC bridge commands"
                        ),
                        prefix = it.getOrDefault(
                            "prefix",
                            prefix,
                            "Prefix for MC bridge commands. Accepts a single character (not alphanumeric or /)"
                        ),
                        authRequests = it.getOrDefault(
                            "authRequests",
                            authRequests,
                            "Enable the 'auth' command for linking chat accounts to uuid / ingame account"
                        ),
                        permisionRequests = it.getOrDefault(
                            "permisionRequests",
                            authRequests,
                            "Enable the 'request' command for requestion permissions from chat"
                        ),
                        defaultPermUnauthenticated = it.getOrDefault(
                            "defaultPermUnauthenticated",
                            defaultPermUnauthenticated,
                            "default permission level for unauthenticated players"
                        ),
                        defaultPermAuthenticated = it.getOrDefault(
                            "defaultPermAuthenticated",
                            defaultPermAuthenticated,
                            "default permission level for players that hve linked their accounts"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, ConnectOptions::class.java) { it, marshaller ->
                with(ConnectOptions()) {
                    ConnectOptions(
                        url = it.getOrDefault(
                            "url",
                            url,
                            "The URL or IP address of the bridge platform"
                        ),
                        gateway = it.getOrDefault(
                            "gateway",
                            gateway,
                            "Gateway ID"
                        ),
                        authToken = it.getOrDefault(
                            "authToken",
                            authToken,
                            "Auth token used to connect to the bridge platform"
                        ),
                        autoConnect = it.getOrDefault(
                            "autoConnect",
                            autoConnect,
                            "Connect the relay on startup"
                        ),
                        reconnectWait = it.getOrDefault(
                            "reconnectWait",
                            reconnectWait,
                            "base delay in milliseconds between attempting reconnects"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, IncomingOptions::class.java) { it, marshaller ->
                with(IncomingOptions()) {
                    IncomingOptions(
                        chat = it.getOrDefault(
                            "chat",
                            chat,
                            "Generic chat event, just talking"
                        ),
                        joinPart = it.getOrDefault(
                            "joinPart",
                            joinPart,
                            "Join and part events from other gateways"
                        ),
                        action = it.getOrDefault(
                            "action",
                            action,
                            "User actions (/me) sent by users from other gateways"
                        ),
                        stripColors = it.getOrDefault(
                            "stripColors",
                            stripColors,
                            "strip colors from incoming text"
                        ),
                        filter = it.getOrDefault(
                            "filter",
                            filter,
                            "Filter incoming messages"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, OutgoingOptions::class.java) { it, marshaller ->
                with(OutgoingOptions()) {
                    OutgoingOptions(
                        systemUser = it.getOrDefault(
                            "systemUser",
                            systemUser,
                            "Name of the platform user (used by death and advancement messages and the /say command)"
                        ),
                        announceConnect = it.getOrDefault(
                            "announceConnect",
                            announceConnect,
                            "announce successful connection to the gateway"
                        ),
                        announceDisconnect = it.getOrDefault(
                            "announceDisconnect",
                            announceConnect,
                            "announce intention to disconnect / reconnect"
                        ),
                        announceReady = it.getOrDefault(
                            "announceReady",
                            announceReady,
                            "announce when the server finished loading the world"
                        ),
                        announceConnectMessage = it.getOrDefault(
                            "announceConnectMessage",
                            announceConnectMessage,
                            "message to send on establishing api connection"
                        ),
                        announceDisconnectMessage = it.getOrDefault(
                            "announceDisconnectMessage",
                            announceDisconnectMessage,
                            "message to send on severing api connection"
                        ),
                        announceReadyMessage = it.getOrDefault(
                            "announceReadyMessage",
                            announceReadyMessage,
                            "message to send when the server loaded the world"
                        ),
                        stripColors = it.getOrDefault(
                            "stripColors",
                            stripColors,
                            "strip colors from nicknames and messages"
                        ),
                        pasteEEKey = it.getOrDefault(
                            "pasteEEKey",
                            pasteEEKey,
                            "paste.ee api key, leave empty to use application default"
                        ),
                        inlineLimit = it.getOrDefault(
                            "inlineLimit",
                            inlineLimit,
                            "messages with more lines than this will get shortened via paste.ee"
                        ),
                        death = it.getOrDefault(
                            "death",
                            DeathOptions(),
                            "Format Options for death messages"
                        ),
                        avatar = it.getOrDefault(
                            "avatar",
                            AvatarOptions(),
                            "Avatar options"
                        ),
                        joinPart = it.getOrDefault(
                            "joinPart",
                            JoinPartOptions(),
                            "format join and part messages to the gateway"
                        ),
                        filter = it.getOrDefault(
                            "filter",
                            filter,
                            "Filter outgoing messages"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, DeathOptions::class.java) { jsonObj, marshaller ->
                with(DeathOptions()) {
                    DeathOptions(
                        damageType = jsonObj.getOrDefault(
                            "damageType",
                            damageType,
                            "Enable Damage type symbols on death messages"
                        ),
                        damageTypeMapping = (jsonObj.getObject("damageTypeMapping")
                            ?: marshaller.serialize(damageTypeMapping) as JsonObject)
                            .let {
                                jsonObj.setComment(
                                    "damageTypMapping",
                                    "Damage type mapping for death cause"
                                )
                                it.mapValues { (key, _) ->
                                    it.getOrDefault(key, damageTypeMapping[key] ?: emptyArray(), key)
                                        .apply { it[key] }.apply {
                                            jsonObj["damageTypeMapping"] = it
                                        }
                                }
                            }
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, AvatarOptions::class.java) { it, marshaller ->
                with(AvatarOptions()) {
                    AvatarOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "enable ingame avatar"
                        ),
                        urlTemplate = it.getOrDefault(
                            "urlTemplate",
                            urlTemplate,
                            "template for constructing the user avatar url using the uuid"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, JoinPartOptions::class.java) { it, marshaller ->
                with(JoinPartOptions()) {
                    JoinPartOptions(
                        joinServer = it.getOrDefault(
                            "joinServer",
                            joinServer,
                            "user join message sent to other gateways, available variables: {username}, {username:antiping}"
                        ),
                        partServer = it.getOrDefault(
                            "partServer",
                            partServer,
                            "user part message sent to other gateways, available variables: {username}, {username:antiping}"
                        )
                    )
                }
            }
            .registerDeserializer(JsonObject::class.java, UpdateOptions::class.java) { it, marshaller ->
                with(UpdateOptions()) {
                    UpdateOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "Enable Update checking"
                        )
                    )
                }
            }
            .build()!!
    }

    fun load(): MatterLinkConfig {
        val jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            logger.severe("error loading config: ${e.completeMessage}")
            jankson.marshaller.serialize(MatterLinkConfig()) as JsonObject
        } catch (e: FileNotFoundException) {
            logger.severe("creating config file $configFile")
            configFile.absoluteFile.parentFile.mkdirs()
            configFile.createNewFile()
            jankson.marshaller.serialize(MatterLinkConfig()) as JsonObject
        }
        logger.info("finished loading base config")

        val tmpCfg = try {
            //cfgDirectory.resolve("debug.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            jankson.fromJson(jsonObject, MatterLinkConfig::class.java).apply {
                configFile.writeText(jsonObject.toJson(true, true))
                logger.info("loaded config: Main config")
                logger.fine("loaded config: $this")
            }
        } catch (e: SyntaxError) {
            logger.severe("error parsing config: ${e.completeMessage} ")
            logger.severe(e.stackTraceString)
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        } catch (e: IllegalStateException) {
            logger.severe(e.stackTraceString)
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        } catch (e: NullPointerException) {
            logger.severe("error loading config: ${e.stackTraceString}")
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        }

//        val defaultJsonObject = jankson.load("{}")
//        jankson.fromJson(defaultJsonObject, MatterLinkConfig::class.java)
//        val nonDefault = jsonObject.getDelta(defaultJsonObject)

        MessageHandler.config.url = tmpCfg.connect.url
        MessageHandler.config.token = tmpCfg.connect.authToken
        MessageHandler.config.gateway = tmpCfg.connect.gateway
        MessageHandler.config.reconnectWait = tmpCfg.connect.reconnectWait

        MessageHandler.config.systemUser = tmpCfg.outgoing.systemUser
        MessageHandler.config.announceConnect = tmpCfg.outgoing.announceConnect
        MessageHandler.config.announceDisconnect = tmpCfg.outgoing.announceDisconnect

        return tmpCfg
    }
}