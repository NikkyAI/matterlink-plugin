package moe.nikky.matterlink.handlers

import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.antiping
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.stripColorOut
import java.util.Random

object DeathHandler {
    private val random = Random()

    @JvmName("handleDeath")
    fun handleDeathJava(
        player: String,
        deathMessage: String,
        damageType: String
    ) = runBlocking {
        handleDeath(
            player = player,
            deathMessage = deathMessage,
            damageType = damageType
        )
    }

    suspend fun handleDeath(
        player: String,
        deathMessage: String,
        damageType: String
//        x: Int = 0, y: Int = 0, z: Int = 0,
//        dimension: Int? = null
    ) {
        if (cfg.outgoing.filter.death) {
            var msg = deathMessage.stripColorOut.replace(player, player.stripColorOut.antiping)
            if (cfg.outgoing.death.damageType) {
                val emojis = cfg.outgoing.death.damageTypeMapping[damageType]
                    ?: arrayOf("\uD83D\uDC7B unknown type '$damageType'")
                val damageEmoji = emojis[random.nextInt(emojis.size)]
                msg += " $damageEmoji"
            }
            LocationHandler.sendToLocations(
                    msg = msg,
//                x = x, y = y, z = z, dimension = dimension,
                    event = ChatEvent.DEATH,
                    cause = "Death Event of $player",
                    systemuser = true
            )
        }
    }
}
