package me.perny.hitman.classes.character.npc

import me.perny.hitman.classes.character.GlowColor
import me.perny.hitman.classes.world.World
import me.perny.hitman.classes.character.signal.Signal
import me.perny.hitman.classes.character.npc.task.TaskPriority
import me.perny.hitman.classes.character.npc.task.tasks.InvestigationTask

open class GuardAINPCCharacter(world: World, uuid: String) : AINPCCharacter(world, uuid, GlowColor.GUARD) {
    override fun tick(tick: Byte) {}
}
