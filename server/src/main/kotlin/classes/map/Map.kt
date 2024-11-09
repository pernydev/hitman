package me.perny.hitman.classes.map

import com.google.gson.Gson
import me.perny.hitman.Global
import me.perny.hitman.ServerMode
import me.perny.hitman.classes.character.npc.AINPCCharacter
import me.perny.hitman.classes.character.npc.GuardAINPCCharacter
import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.world.World
import me.perny.hitman.editor.spawnNPC
import java.io.File
import java.util.UUID

abstract class Map {
    abstract var id: String
    private var npcData: String = "[]"

    fun loadNPCData(world: World) {
        val npcData = File("maps/$id/npcs.json").readText()

        val npcs: List<SavedNPC> = Gson().fromJson(npcData, Array<SavedNPC>::class.java).toList()
        if (Global.serverMode == ServerMode.EDITOR) {
            loadNPCsToEditor(npcs, world)
            return
        }
        npcs.forEach {
            when (it.type) {
                NPCSpawnType.NPC -> addNPC(it, world)
                NPCSpawnType.AI_NPC -> addAINPC(it, world)
                NPCSpawnType.GUARD_NPC -> addGuardNPC(it, world)
                NPCSpawnType.BOUNCER_NPC -> addBouncerNPC(it, world)
            }
        }
    }

    abstract fun npcCustomLogic(npc: AINPCCharacter, savedNPC: SavedNPC)

    fun addNPC(npc: SavedNPC, world: World) {
        val uuid = UUID.randomUUID().toString()
        world.addCharacter(NPCCharacter(world, uuid).apply { position = npc.position })
    }

    fun addAINPC(npc: SavedNPC, world: World) {
        val uuid = UUID.randomUUID().toString()
        val aiNPC = AINPCCharacter(world, uuid).apply { position = npc.position }
        npcCustomLogic(aiNPC, npc)
        world.addCharacter(aiNPC)
    }

    fun addGuardNPC(npc: SavedNPC, world: World) {
        val uuid = UUID.randomUUID().toString()
        val guardNPC = GuardAINPCCharacter(world, uuid).apply { position = npc.position }
        npcCustomLogic(guardNPC, npc)
        world.addCharacter(guardNPC)
    }

    fun addBouncerNPC(npc: SavedNPC, world: World) {
        TODO()
    }

    fun loadNPCsToEditor(npcs: List<SavedNPC>, world: World) {
        npcs.forEach {
            spawnNPC(it.position, it.type, world.instanceContainer, null, it.id, it.position.yaw)
        }
    }
}


