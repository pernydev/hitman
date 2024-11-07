package me.perny.hitman.editor

import com.google.gson.Gson
import me.perny.hitman.Global
import me.perny.hitman.classes.map.NPCSpawnType
import me.perny.hitman.classes.map.SavedNPC
import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.ArmorStandMeta
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.apache.commons.io.FileUtils
import java.io.File


fun Player.giveEditorInventory() {
    val inventory: Map<Int, ItemStack> = mapOf(
        0 to ItemStack.of(Material.STICK)
            .builder()
            .customName(Component.text("NPC"))
            .set(Tag.String("editoritem"), "npc")
            .build(),
        1 to ItemStack.of(Material.STICK)
            .builder()
            .customName(Component.text("AI NPC"))
            .set(Tag.String("editoritem"), "ainpc")
            .build(),
        2 to ItemStack.of(Material.STICK)
            .builder()
            .customName(Component.text("Guard AI NPC"))
            .set(Tag.String("editoritem"), "guardnpc")
            .build(),
        3 to ItemStack.of(Material.STICK)
            .builder()
            .customName(Component.text("Bouncer Guard AI NPC"))
            .set(Tag.String("editoritem"), "bouncernpc")
            .build(),
    )

    inventory.forEach { (slot, item) ->
        this.inventory.setItemStack(slot, item)
    }
}

fun onInteract(playerUseItemOnBlockEvent: PlayerUseItemOnBlockEvent) {
    if (!playerUseItemOnBlockEvent.itemStack.hasTag(Tag.String("editoritem"))) return

    val type = stringToNPCSpawnType(playerUseItemOnBlockEvent.itemStack.getTag(Tag.String("editoritem")))
    spawnNPC(
        playerUseItemOnBlockEvent.position.add(0.5, 1.0, 0.5),
        type,
        playerUseItemOnBlockEvent.instance,
        playerUseItemOnBlockEvent.player,
        null,
    )
}

var selectedNPC: EditorNPC? = null
val editorNPCs = mutableListOf<EditorNPC>()

data class EditorNPC(val entity: Entity, val type: NPCSpawnType, val id: Int)

fun onEntityInteract(event: PlayerEntityInteractEvent) {
    if (event.target.entityType != EntityType.ARMOR_STAND) return

    println("removing entity")

    if (selectedNPC?.entity == event.target) {
        selectedNPC?.entity?.entityMeta?.isHasGlowingEffect = false
        selectedNPC = null
        return
    }

    selectedNPC?.entity?.entityMeta?.isHasGlowingEffect = false
    event.target.entityMeta.isHasGlowingEffect = true
    selectedNPC = editorNPCs.first { it.entity == event.target }
}

fun spawnNPC(
    point: Point,
    type: NPCSpawnType,
    instance: Instance,
    player: Player?,
    id: Int? = null,
    yaw: Float? = null
) {
    val entity = LivingEntity(EntityType.ARMOR_STAND)
    val meta = entity.entityMeta as ArmorStandMeta
    val npcID = id ?: if (type == NPCSpawnType.NPC) 0 else editorNPCs.filter { it.type != NPCSpawnType.NPC }.size

    editorNPCs.add(EditorNPC(entity, type, npcID))
    meta.customName = when (type) {
        NPCSpawnType.NPC -> Component.text("NPC")
        NPCSpawnType.AI_NPC -> Component.text("AI NPC ($npcID)")
        NPCSpawnType.GUARD_NPC -> Component.text("Guard AI NPC ($npcID)")
        NPCSpawnType.BOUNCER_NPC -> Component.text("Bouncer Guard AI NPC ($npcID)")
    }
    meta.isCustomNameVisible = true
    entity.setTag(Tag.String("editorentity"), type.string())
    entity.helmet = ItemStack.of(Material.PLAYER_HEAD)
    var pos = Pos.fromPoint(point)

    pos = if (player != null) {
        pos.withYaw(snapRotation(player.position.yaw + 180))
    } else {
        pos.withYaw(yaw ?: 0f)
    }
    entity.setInstance(instance, pos)
}

fun snapRotation(yaw: Float): Float {
    // snap rotation to 22.5 degree increments
    return (yaw / 22.5).toInt() * 22.5f
}

fun NPCSpawnType.string(): String {
    return when (this) {
        NPCSpawnType.NPC -> "npc"
        NPCSpawnType.AI_NPC -> "ainpc"
        NPCSpawnType.GUARD_NPC -> "guardnpc"
        NPCSpawnType.BOUNCER_NPC -> "bouncernpc"
    }
}

fun stringToNPCSpawnType(string: String): NPCSpawnType {
    return when (string) {
        "npc" -> NPCSpawnType.NPC
        "ainpc" -> NPCSpawnType.AI_NPC
        "guardnpc" -> NPCSpawnType.GUARD_NPC
        "bouncernpc" -> NPCSpawnType.BOUNCER_NPC
        else -> throw IllegalArgumentException("Invalid NPC type")
    }
}

class DeleteCommand : Command("delete", "del") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (selectedNPC == null) {
                sender.sendMessage(Component.text("No NPC selected"))
                return@CommandExecutor
            }

            selectedNPC?.entity?.remove()
            editorNPCs.remove(selectedNPC)
        }
    }
}

class SaveCommand : Command("save", "s") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            val savedNPCs = editorNPCs.map {
                SavedNPC(it.type, Pos.fromPoint(it.entity.position), it.id)
            }

            val json = Gson().toJson(savedNPCs)
            FileUtils.writeStringToFile(File("maps/${Global.map?.id}/npcs.json"), json)
        }
    }
}