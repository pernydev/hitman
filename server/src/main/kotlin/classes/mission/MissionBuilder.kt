package me.perny.hitman.classes.game

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.character.PlayableCharacter
import me.perny.hitman.classes.map.Map
import me.perny.hitman.classes.world.World
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import java.util.UUID

class MissionBuilder {
    private val players = mutableListOf<Player>()
    private val npcs = mutableListOf<Pos>()
    private var map: Map? = null

    fun addPlayer(player: Player): MissionBuilder {
        players.add(player)
        return this
    }

    fun addDummyNPC(pos: Pos): MissionBuilder {
        npcs.add(pos)
        return this
    }

    fun useMap(map: Map): MissionBuilder {
        this.map = map
        return this
    }

    fun build(): Mission {
        val world = World(map!!)

        players.forEach {
            val uuid = UUID.randomUUID().toString()
            world.addPlayableCharacter(PlayableCharacter(world, it, uuid))
        }

        npcs.forEach {
            val uuid = UUID.randomUUID().toString()
            world.addCharacter(NPCCharacter(world, uuid).apply { position = it })
        }

        if (map == null) {
            throw IllegalStateException("Map must be set before building a mission")
        }

        return Mission(world, map!!)
    }
}
