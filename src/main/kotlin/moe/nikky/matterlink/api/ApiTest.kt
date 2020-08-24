package moe.nikky.matterlink.api

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.result.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import moe.nikky.matterlink.MessageHandler
import moe.nikky.matterlink.api.Config
import moe.nikky.matterlink.config.BaseConfig
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.jsonNonstrict
import moe.nikky.matterlink.logger
import java.io.Reader
import java.util.logging.Logger

fun main() = runBlocking {
    logger = Logger.getGlobal()
    cfg = BaseConfig.MatterLinkConfig(connect = BaseConfig.ConnectOptions(gateway = "matterlink"))
//    MessageHandler.config = Config(
//        url = "http://localhost:4242",
//        token = "",
//        gateway = "minecraft",
//        systemUser = "Test"
//    )
//    MessageHandler.start("Test started, connecting to matterbridge API", false)


//    MessageHandler.transmit(ApiMessage(text = "test"))

//    MessageHandler.transmit(
//        ApiMessage(
//            "username",
//            "test message",
//            "matterlink"
//        )
//    )

    messageFlow().collect { msg: ApiMessage ->
       println("received: $msg")
    }

    Unit
}

val config = Config(
        url = "http://localhost:4242",
        token = "",
        gateway = "minecraft",
        systemUser = "Test"
)
private var enabled = false

private var connectErrors = 0
private var reconnectCooldown = 0L
private var sendErrors = 0

private val keepOpenManager = FuelManager().apply {
    timeoutInMillisecond = 0
    timeoutReadInMillisecond = 0
}

private fun messageFlow() = channelFlow<ApiMessage> { //channel ->
    launch(context = Dispatchers.IO + CoroutineName("msgReceiver")) {
        loop@ while (isActive) {
            logger.info("opening connection")
            val url = "${config.url}/api/stream"
            val (request, response, result) = keepOpenManager.request(Method.GET, url)
                    .apply {
                        if (config.token.isNotEmpty()) {
                            headers["Authorization"] = "Bearer ${config.token}"
                        }
                    }
                    .responseObject(object : ResponseDeserializable<Unit> {
                        override fun deserialize(reader: Reader) {
                            //                        runBlocking(Dispatchers.IO + CoroutineName("msgDecoder")) {
                            logger.info("connected successfully")
                            connectErrors = 0
                            reconnectCooldown = 0

                            reader.useLines { lines ->
                                lines.forEach { line ->
                                    val msg = jsonNonstrict.parse(ApiMessage.serializer(), line)
                                    logger.fine("received: $msg")
                                    if (msg.event != "api_connect") {
                                        runBlocking {
                                            channel.send(msg)
                                        }
                                        // messageBroadcastInput.send(msg)
                                    }
                                }
                            }
                        }
                        // }
                    })

            when (result) {
                is Result.Success -> {
                    logger.info("connection closed")
                }
                is Result.Failure -> {
                    logger.info("connection closed")
                    connectErrors++
                    reconnectCooldown = connectErrors * 1000L
                    logger.severe("connectErrors: $connectErrors")
                    logger.severe("connection error")
                    logger.severe("curl: ${request.cUrlString()}")
                    logger.severe(result.error.localizedMessage)
                    result.error.exception.printStackTrace()
                    if (connectErrors >= 10) {
                        logger.severe("Caught too many errors, closing bridge")
//                        stop("Interrupting connection to matterbridge API due to accumulated connection errors")
                        break@loop
                    }
                }
            }
            delay(reconnectCooldown) // reconnect delay in ms
        }
    }
}