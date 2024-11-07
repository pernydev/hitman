package me.perny.hitman.classes.map

import net.minestom.server.coordinate.Pos

data class SavedNPC(val type: NPCSpawnType, val position: Pos, val id: Int)

enum class NPCSpawnType {
    NPC,
    AI_NPC,
    GUARD_NPC,
    BOUNCER_NPC,
}
