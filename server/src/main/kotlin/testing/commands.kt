package me.perny.hitman.debug

import me.perny.hitman.Global
import me.perny.hitman.classes.debugger.d
import me.perny.hitman.classes.debugger.subscribedCharacters
import me.perny.hitman.classes.pack.getRequest
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player

class PackCommand : Command("pack", "p") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            d("Resourcepack sent", "testing")
            sender.sendResourcePacks(getRequest())
        }
    }
}

class GamemodeCommand : Command("gm") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            val player = sender as Player
            player.gameMode = if (player.gameMode == GameMode.CREATIVE) GameMode.SURVIVAL else GameMode.CREATIVE
        }
    }
}

class StartCommand : Command("start") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            Global.mission?.start()
        }
    }
}
