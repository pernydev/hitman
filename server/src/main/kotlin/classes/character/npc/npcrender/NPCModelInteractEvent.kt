package me.perny.hitman.classes.character.npc.npcrender

import net.minestom.server.entity.Player
import net.minestom.server.entity.Player.Hand
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.worldseed.multipart.GenericModel
import net.worldseed.multipart.events.ModelEvent
import net.worldseed.multipart.model_bones.BoneEntity

class NPCModelInteractEvent(private val model: GenericModel, event: PlayerEntityInteractEvent, val bone: BoneEntity?) :
    ModelEvent {
    val interacted: Player = event.player

    val hand: Hand = event.hand

    constructor(model: NPCModel, event: PlayerEntityInteractEvent) : this(model, event, null as BoneEntity?)

    override fun model(): GenericModel {
        return this.model
    }
}
