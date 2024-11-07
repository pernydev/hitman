package me.perny.hitman.classes.character.npc.pathfinding

import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag

class BlockGetter(private val originalGetter: Block.Getter) : Block.Getter {

    override fun getBlock(x: Int, y: Int, z: Int, condition: Block.Getter.Condition): Block {
        // Otherwise delegate to original getter
        val block: Block;
        try {
            block = originalGetter.getBlock(x, y, z, condition)
        } catch (e: Exception) {
            return Block.AIR
        }


        if (block.hasTag(Tag.Boolean("door"))) {
            println("Door found")
            return Block.AIR // Pathfinding should ignore doors
        }

        if (block.name().contains("structure_void")) {

            return Block.BARRIER // Pathfinding should consider structure_void as a solid block
        }

        return block
    }
}