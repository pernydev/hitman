package me.perny.hitman.classes

import net.minestom.server.entity.PlayerSkin

class Skin(
    var clothing: Clothing,
    var isPlayer: Boolean,
    var voice: Int,
) {
    fun getPlayerSkin(): PlayerSkin {
        return PlayerSkin.fromUsername("pernydev") ?: throw IllegalArgumentException("Skin not found")
    }
}

enum class Clothing {
    SUIT,
    SECURITY_GUARD,
    COOK,
}
