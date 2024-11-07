package me.perny.hitman.classes.map.test

import me.perny.hitman.classes.character.npc.AINPCCharacter
import me.perny.hitman.classes.map.Map
import me.perny.hitman.classes.map.SavedNPC

class TestMap : Map() {
    override var id: String = "test"

    override fun npcCustomLogic(npc: AINPCCharacter, savedNPC: SavedNPC) {
        when (savedNPC.id) {
            else -> {}
        }
    }
}

