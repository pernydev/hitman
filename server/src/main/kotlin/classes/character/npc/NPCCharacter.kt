package me.perny.hitman.classes.character.npc

import me.perny.hitman.classes.character.Character
import me.perny.hitman.classes.character.npc.npcrender.NPCRenderer
import me.perny.hitman.classes.world.World
import me.perny.hitman.classes.character.signal.DefaultSignalListener
import me.perny.hitman.classes.character.signal.Signal
import me.perny.hitman.classes.character.signal.SignalChannel
import me.perny.hitman.classes.character.signal.SignalListener
import me.perny.hitman.classes.character.speak.SpeakManager
import me.perny.hitman.classes.character.speak.getSingleLine
import me.perny.hitman.classes.character.npc.task.TaskManager
import me.perny.hitman.classes.character.npc.task.TaskPriority
import me.perny.hitman.classes.character.npc.task.tasks.DestinationTask
import me.perny.hitman.classes.world.AreaType
import me.perny.hitman.classes.world.findGround
import me.perny.hitman.classes.world.randomPosition
import me.perny.hitman.utils.findNearbyRandomLocation
import net.minestom.server.coordinate.Pos

open class NPCCharacter(world: World) : Character(world), SignalListener by DefaultSignalListener() {
    val taskManager = TaskManager(null, this)
    val speakManager = SpeakManager(this)
    var npcrenderer: NPCRenderer? = null

    override fun spawn() {
        taskManager.update()
        npcrenderer = NPCRenderer(this)

        taskManager.addTask(
            DestinationTask(
                priority = TaskPriority.LOW,
                destination = findNearbyRandomLocation(40),
                run = false,
                allowUnfinished = true
            )
        )
    }

    override fun nonblockingTick() {
        world.playableCharacters.first().let { target ->
            val distance = target.position.distance(position)
            if (distance < 1) {
                distanceAnnoyed()
            }
        }
    }

    /**
     * A player is too close for comfort.
     * Non-AI NPCs won't actually do anything else than speak
     */
    private fun distanceAnnoyed() {
        speakManager.sayLine(getSingleLine("distanceAnnoyed"))
    }

    override fun danger(signal: Signal) {
        runForShelter()
    }

    /**
     * The NPC observes a danger and calls for help.
     */
    fun callForHelp() {
        Signal(position, SignalChannel.HELP, 20, world).send()
        Signal(position, SignalChannel.DANGER, 20, world).send()
    }

    private fun runForShelter() {
        val shelterArea = world.findNearestArea(this, AreaType.SHELTER) ?: return
        val position = shelterArea.randomPosition().findGround(world) ?: return
        goto(position, true)
    }

    private fun goto(position: Pos, run: Boolean = false) {
        taskManager.addTask(
            DestinationTask(
                priority = TaskPriority.HIGH,
                destination = position,
                run = run,
                allowUnfinished = true
            )
        )
    }


    override fun tick(tick: Byte) {}
}