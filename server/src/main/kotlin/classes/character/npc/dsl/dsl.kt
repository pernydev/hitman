package me.perny.hitman.classes.character.npc.dsl

import net.minestom.server.coordinate.Pos

open class Sequence(val actions: List<Action>, val looping: Boolean = false) {

}
open class Action(val action: ActionType) {
}

class DSLNPC {}

class Trigger() {}

// Main DSL class for defining tasks
class TaskDSL {
    private val sequences = mutableListOf<Sequence>()
    private val actions = mutableListOf<Action>()

    // Adds a sequence of actions
    fun sequence(atTime: Double, looping: Boolean = false, block: SequenceDSL.() -> Unit) {
        val sequenceDSL = SequenceDSL(atTime, looping = looping)
        sequenceDSL.apply(block)
        sequences.add(sequenceDSL.build())
    }

    fun sequence(trigger: Trigger, looping: Boolean = false, block: SequenceDSL.() -> Unit) {
        val sequenceDSL = SequenceDSL(trigger = trigger, looping = looping)
        sequenceDSL.apply(block)
        sequences.add(sequenceDSL.build())
    }


    // randomizes actions inside of it.
    fun randomize(vararg actions: Action) {
        this.actions.add(RandomizeAction(actions.toList()))
    }
}

enum class ActionType {
    WALK, TALK, FOLLOW, RANDOMIZE
}

class RandomizeAction(val actions: List<Action>) : Action(ActionType.RANDOMIZE) {}

class FollowAction(val follower: DSLNPC, val target: DSLNPC) : Action(ActionType.FOLLOW) {
    var untilTime: Double? = null
    var forTime: Double? = null

    fun untilTime(time: Double): Action {
        untilTime = time
        return this
    }

    fun forTime(time: Double): Action {
        forTime = time
        return this
    }
}

// Sequence DSL to handle NPC actions
class SequenceDSL(val at: Double? = null, trigger: Trigger? = null, val looping: Boolean = false) {
    private val actions = mutableListOf<Action>()

    infix fun DSLNPC.walk(to: Pos): Action {
        val action = Action(ActionType.WALK)
        actions.add(action)
        return action
    }

    infix fun DSLNPC.talkTo(other: DSLNPC): Action {
        val action = Action(ActionType.TALK)
        actions.add(action)
        return action
    }

    infix fun DSLNPC.follow(other: DSLNPC): FollowAction {
        actions.add(Action(ActionType.FOLLOW))
        return FollowAction(this, other)
    }

    infix fun FollowAction.untilTime(time: Double) {
        actions.add(untilTime(time))
    }

    infix fun FollowAction.forTime(time: Double) {
        actions.add(forTime(time))
    }

    fun build(isLooping: Boolean = false): Sequence {
        return Sequence(actions, looping = isLooping)
    }
}