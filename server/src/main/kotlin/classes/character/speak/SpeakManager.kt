package me.perny.hitman.classes.character.speak

import me.perny.hitman.classes.character.Character
import net.kyori.adventure.sound.Sound
import net.minestom.server.network.packet.server.play.SoundEffectPacket
import net.minestom.server.sound.SoundEvent

class SpeakManager(val character: Character) {
    var currentConversation: Conversation? = null

    private fun sendSound(id: String) {
        val packet = SoundEffectPacket(
            SoundEvent.of(getSoundNamespacedId(id), 10f),
            Sound.Source.VOICE,
            character.position.x.toInt(),
            character.position.y.toInt(),
            character.position.z.toInt(),
            1f,
            1f,
            0
        )
//        character.world.playableCharacters.forEach { it.player.sendPacket(packet) }
    }

    private fun getSoundNamespacedId(id: String): String {
        return "hitman:speak.${character.skin.voice}.${id}"
    }

    fun sayLine(line: String) {
        sendSound(line)
    }
}