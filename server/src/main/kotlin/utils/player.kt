package me.perny.hitman.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.attribute.AttributeInstance
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import net.minestom.server.tag.Tag
import java.sql.Time

fun Player.hideFromTab() {
    val packet = PlayerInfoUpdatePacket(
        PlayerInfoUpdatePacket.Action.UPDATE_LISTED,
        PlayerInfoUpdatePacket.Entry(
            this.uuid,
            null,
            listOf<PlayerInfoUpdatePacket.Property>(),
            false,
            0,
            null,
            null,
            null,
        )
    )

    MinecraftServer.getConnectionManager().onlinePlayers.forEach {
        it.sendPacket(packet)
    }
}

class ShaderMarker(val player: Player, val color: Int, val type: ShaderMarkerType) {
    var entity: Entity? = null
    fun spawn() {
        entity = Entity(EntityType.TEXT_DISPLAY)
        entity?.setInstance(player.instance, player.position)
        entity?.editEntityMeta(TextDisplayMeta::class.java) {
            it.text = Component.text(Glyphs.SQUARE).color(TextColor.color(color))
            it.backgroundColor = 0
        }
        entity?.isAutoViewable = false
        entity?.addViewer(player)
        entity?.setInstance(player.instance)
        player.addPassenger(entity!!)
    }

    fun despawn() {
        entity?.remove()
        entity = null
    }
}

val requiredShaderMarkers = mutableListOf<ShaderMarker>()

enum class ShaderMarkerType(val color: Int) {
    INSTINCT(0x0000fe),
}

fun Player.addShaderMarker(marker: ShaderMarkerType) {
    val markerEntity = ShaderMarker(this, marker.color, marker)
    markerEntity.spawn()
    requiredShaderMarkers.add(markerEntity)
}

fun Player.removeShaderMarker(marker: ShaderMarkerType) {
    requiredShaderMarkers.forEach {
        if (it.player == this && it.type == marker) {
            it.despawn()
            requiredShaderMarkers.remove(it)
            return
        }
    }
}

val Player.isInInstinct: Boolean
    get() = this.getTag(Tag.Boolean("instinct")) ?: false

fun Player.enableInstinct() {
    this.setTag(Tag.Boolean("instinct"), true)
    addShaderMarker(ShaderMarkerType.INSTINCT)
    this.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = 0.05
}

fun Player.disableInstinct() {
    this.setTag(Tag.Boolean("instinct"), false)
    removeShaderMarker(ShaderMarkerType.INSTINCT)
    this.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = 0.1
}

val lastInstinctPing = mutableMapOf<Player, Long?>()

fun Player.instinctPing() {
    if (!isInInstinct) {
        enableInstinct()
        lastInstinctPing[this] = System.currentTimeMillis() + 700
        return
    }
    lastInstinctPing[this] = System.currentTimeMillis()
}

fun instinctPingTask() {
    lastInstinctPing.forEach {
        if (it.value == null) return@forEach
        if (System.currentTimeMillis() - it.value!! > 100) {
            it.key.disableInstinct()
        }
    }
}