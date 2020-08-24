package moe.nikky.matterlink.handlers

import moe.nikky.matterlink.antiping
import moe.nikky.matterlink.config.cfg
import moe.nikky.matterlink.stripColorOut


object ProgressHandler {

    suspend fun handleProgress(
        name: String, message: String, advancementKey: String
//        x: Int, y: Int, z: Int,
//        dimension: Int
    ) {
        if (!cfg.outgoing.filter.advancement) return
        if(advancementKey.startsWith("recipes/")) return
        val usr = name.stripColorOut.antiping
        LocationHandler.sendToLocations(
            msg = "$usr $message $advancementKey".stripColorOut,
//            x = x, y = y, z = z, dimension = dimension,
            event = ChatEvent.ADVANCEMENT,
            cause = "Progress Event by $usr",
            systemuser = true
        )
    }
}