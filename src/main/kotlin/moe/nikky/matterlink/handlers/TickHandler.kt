package moe.nikky.matterlink.handlers

import kotlinx.coroutines.runBlocking
/**
 * Created by nikky on 21/02/18.
 * @author Nikky
 * @version 1.0
 */
object TickHandler : Runnable {

    var tickCounter = 0
        private set
    private var accumulator = 0
    private const val updateInterval = 12 * 60 * 60 * 20

    override fun run() = runBlocking {
        handleTick()
    }

    suspend fun handleTick() {
        tickCounter++

//        ServerChatHandler.writeIncomingToChat()

//        if (accumulator++ > updateInterval) {
//            accumulator -= updateInterval
//            UpdateChecker.check()
//        }
    }

}