package me.perny.hitman.classes

import me.perny.hitman.skin.Overlays
import me.perny.hitman.skin.getOverlayedSkin
import net.minestom.server.entity.PlayerSkin

class Skin(
    var clothing: Clothing,
    var isPlayer: Boolean,
    var voice: Int,
) {
    fun getPlayerSkin(): PlayerSkin {
        return getOverlayedSkin(PlayerSkin.fromUsername("pernydev")!!, Overlays.GUARD)?.toSkin() ?: PlayerSkin.fromUsername("pernydev")!!
    }
}

enum class Clothing {
    SUIT,
    SECURITY_GUARD,
    COOK,
}
