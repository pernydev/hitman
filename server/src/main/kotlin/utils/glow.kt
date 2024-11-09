package me.perny.hitman.utils

import me.perny.hitman.classes.character.GlowColor

fun getGlowColorValue(glowColor: GlowColor?): Int {
    return when (glowColor) {
        GlowColor.NPC -> 0x0000fd
        GlowColor.PLAYER -> 0x0000fc
        GlowColor.GUARD -> 0x0000fb
        else -> 0x0000fa
    }
}