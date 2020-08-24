package moe.nikky.matterlink

import kotlinx.coroutines.runBlocking
import moe.nikky.matterlink.handlers.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent

class MatterlinkListener : Listener {
    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        println("reveived: $event")

        runBlocking {
            ChatProcessor.sendToBridge(
                    event.player.displayName,
                    event.message,
                    ChatEvent.PLAIN,
                    event.player.uniqueId
            )
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        println("event: $event")
        println(event.player)
        println(event.joinMessage)
        runBlocking {
            JoinLeaveHandler.handleJoin(
                    event.player.displayName,
                    event.joinMessage!!
            )
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        println("event: $event")
        println(event.player)
        println(event.quitMessage)
        runBlocking {
            JoinLeaveHandler.handleLeave(
                    event.player.displayName,
                    event.quitMessage!!
            )
        }
    }

    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        println("event: $event")
        println(event.player)
        println(event.advancement)
        println(event.advancement.key.key)
        runBlocking {
            ProgressHandler.handleProgress(
                    event.player.displayName,
                    "has made the advancement",
                    event.advancement.key.key
            )
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        println("event: $event")
        println(event.entity.lastDamageCause!!.cause)
        println(event.deathMessage)
//        println(event.entity.lastDamageCause.eventName)
        runBlocking {
            DeathHandler.handleDeath(
                    event.entity.displayName,
                    event.deathMessage!!,
                    event.entity.lastDamageCause!!.cause.name
            )
        }
    }

    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        println("event: $event")
        println(event.type)
    }
}
