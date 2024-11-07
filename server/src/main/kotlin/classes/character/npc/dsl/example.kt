package me.perny.hitman.classes.character.npc.dsl

import net.minestom.server.coordinate.Pos

// Example on how the DSL can be used

fun example() {
    val npc = DSLNPC()
    val jeff = DSLNPC()
    val taskDSL = TaskDSL()

    taskDSL.apply {
        sequence(atTime = 0.0) { // As soon as the mission starts
            npc walk Pos(10.0, 0.0, 10.0)
            npc walk Pos(20.0, 0.0, 20.0)
            randomize( // run one of these actions
                npc walk Pos(30.0, 0.0, 30.0),
                npc walk Pos(-30.0, 0.0, 10.0)
            )
        }

        val panic = Trigger() // Create a trigger "panic"

        sequence(trigger = panic) { // When the panic trigger is activated
            npc follow jeff forTime 10.0
        }
    }
}