package moe.nikky.matterlink.api

data class Config(
    var url: String = "",
    var token: String = "",
    var gateway: String = "",
    var announceConnect: Boolean = true,
    var announceDisconnect: Boolean = true,
    var reconnectWait: Long = 500,
    var systemUser: String = "Server"
)