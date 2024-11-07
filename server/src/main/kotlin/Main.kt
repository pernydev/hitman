package me.perny.hitman

import me.perny.hitman.classes.character.npc.npcrender.loadAnimations
import me.perny.hitman.classes.game.Mission
import me.perny.hitman.classes.game.MissionBuilder
import me.perny.hitman.classes.map.test.TestMap
import me.perny.hitman.editor.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.*
import net.minestom.server.event.server.ServerTickMonitorEvent
import net.minestom.server.timer.TaskSchedule
import net.worldseed.resourcepack.PackBuilder
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.Path
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.min


fun main() {
    val minecraftServer = MinecraftServer.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    val base_path = Path.of("src/main/resources")
    val model_path = base_path.resolve("models")

    FileUtils.copyDirectory(base_path.resolve("resourcepack_template").toFile(), base_path.resolve("resourcepack").toFile());
    val config = PackBuilder.Generate(base_path.resolve("bbmodel"), base_path.resolve("resourcepack"), model_path);
    FileUtils.writeStringToFile(base_path.resolve("model_mappings.json").toFile(), config.modelMappings(),
        Charset.defaultCharset().toString()
    );

    when (Global.serverMode) {
        ServerMode.EDITOR -> {
            MinecraftServer.getCommandManager().register(DeleteCommand())
            MinecraftServer.getCommandManager().register(SaveCommand())
        }
        else -> {}
    }

    loadAnimations()

    var mission: Mission? = null

    val globalEventHandler = MinecraftServer.getGlobalEventHandler();
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        event.spawningInstance = instanceContainer
        mission = MissionBuilder()
            .addPlayer(event.player)
            .useMap(TestMap())
            .build()
    }

    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if (mission?.hasStarted == true) return@addListener
        if (Global.serverMode == ServerMode.EDITOR) {
            event.player.giveEditorInventory()
            event.player.setGameMode(GameMode.CREATIVE)
            event.player.sendMessage("Welcome to the editor!")
            event.player.flyingSpeed = 0.2f
        }
        mission?.start()
    }

    if (Global.serverMode != ServerMode.PRODUCTION) {
        val bossBar = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS)
        val dec = DecimalFormat("0.00")
        MinecraftServer.getGlobalEventHandler().addListener(
            ServerTickMonitorEvent::class.java
        ) { e: ServerTickMonitorEvent ->
            val tickTime = floor(e.tickMonitor.tickTime * 100.0) / 100.0
            bossBar.name(
                Component.text()
                    .append(Component.text("MSPT: " + dec.format(tickTime)))
            )
            bossBar.progress(
                min((tickTime.toFloat() / MinecraftServer.TICK_MS.toFloat()).toDouble(), 1.0).toFloat()
            )
            if (tickTime > MinecraftServer.TICK_MS) {
                bossBar.color(BossBar.Color.RED)
            } else {
                bossBar.color(BossBar.Color.GREEN)
            }
        }
        MinecraftServer.getGlobalEventHandler().addListener(
            PlayerSpawnEvent::class.java
        ) { e: PlayerSpawnEvent ->
            e.player.showBossBar(bossBar)
        }
    }

    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        if (mission?.hasStarted == true) {
            mission?.world?.playableCharacters?.removeIf { it.player == event.player }
        }
        if (mission?.world?.playableCharacters?.isEmpty() == true) {
            mission = null
        }
    }

    globalEventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
        if (Global.serverMode == ServerMode.EDITOR) {
            return@addListener
        }
    }

    globalEventHandler.addListener(PlayerUseItemOnBlockEvent::class.java) { event ->
        if (Global.serverMode == ServerMode.EDITOR) {
            onInteract(event)
            return@addListener
        }
    }

    globalEventHandler.addListener(PlayerEntityInteractEvent::class.java) { event ->
        if (Global.serverMode == ServerMode.EDITOR) {
            onEntityInteract(event)
            return@addListener
        }
    }

    MinecraftServer.getSchedulerManager().submitTask {
        mission?.world?.tick(0)
        return@submitTask TaskSchedule.tick(1)
    }

    minecraftServer.start("0.0.0.0", 25565)
}