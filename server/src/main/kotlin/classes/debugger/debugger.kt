package me.perny.hitman.classes.debugger

import com.mattworzala.debug.DebugMessage
import com.mattworzala.debug.Layer
import com.mattworzala.debug.shape.LineShape
import com.mattworzala.debug.shape.Shape
import me.perny.hitman.Global
import me.perny.hitman.ServerMode
import me.perny.hitman.classes.character.Character
import me.perny.hitman.utils.Glyphs
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player

val builder = DebugMessage.builder()

val subscribedCharacters: Map<Player, Character> = mutableMapOf()

fun Character.d(message: String) {
    if (Global.serverMode == ServerMode.PRODUCTION) return
    sendCharacterDebugMessage(this, prefixMessage(message))
}

fun Character.d(block: Runnable) {
    if (Global.serverMode == ServerMode.PRODUCTION) return
    block.run()
}

fun Character.dLine(name: String, color: Int, points: List<Pos>) {
    if (Global.serverMode == ServerMode.PRODUCTION) return
    val line = Shape.line()
        .type(LineShape.Type.STRIP)
        .color(0xFF00FF00.toInt())
        .lineWidth(4f)

    points.forEach {
        line.point(it)
    }

    val packet = builder.set(
       "${this.uuid}:$name",
        line.build()
    ).build().packet

    this.world.playableCharacters.forEach {
//        it.player.sendPacket(packet)
    }
}

fun prefixMessage(message: String): Component {
    return Component.text("${Glyphs.DEBUG} $message")
}


fun sendCharacterDebugMessage(character: Character, message: Component) {
    runForSubscribers(character) {
        it.sendMessage(message)
    }
}

fun runForSubscribers(character: Character, block: (Player) -> Unit) {
    character.world.playableCharacters.forEach {
        block(it.player)
    }
}

fun d(message: String, sender: String) {
    if (Global.serverMode == ServerMode.PRODUCTION) return
    // Generic debug message
    MinecraftServer.getConnectionManager().onlinePlayers.forEach {
        it.sendMessage(prefixMessage("$sender: $message"))
    }
}
