package me.perny.hitman

import me.perny.hitman.classes.character.PlayableCharacter
import me.perny.hitman.classes.character.npc.npcrender.loadAnimations
import me.perny.hitman.classes.game.MissionBuilder
import me.perny.hitman.classes.map.test.TestMap
import me.perny.hitman.classes.pack.getRequest
import me.perny.hitman.debug.GamemodeCommand
import me.perny.hitman.debug.PackCommand
import me.perny.hitman.debug.StartCommand
import me.perny.hitman.editor.*
import me.perny.hitman.skin.Overlays
import me.perny.hitman.skin.getOverlayedSkin
import me.perny.hitman.utils.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.chat.SignedMessage.signature
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.*
import net.minestom.server.event.server.ServerTickMonitorEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.TeamBuilder
import net.worldseed.resourcepack.PackBuilder
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.Path
import java.text.DecimalFormat
import java.time.Duration
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.min


fun main() {
    val minecraftServer = MinecraftServer.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    val base_path = Path.of("src/main/resources")
    val model_path = base_path.resolve("models")

    MojangAuth.init();

    FileUtils.copyDirectory(
        base_path.resolve("resourcepack_template").toFile(),
        base_path.resolve("resourcepack").toFile()
    );
    val config = PackBuilder.Generate(base_path.resolve("bbmodel"), base_path.resolve("resourcepack"), model_path);
    FileUtils.writeStringToFile(
        base_path.resolve("model_mappings.json").toFile(), config.modelMappings(),
        Charset.defaultCharset().toString()
    );

    when (Global.serverMode) {
        ServerMode.EDITOR -> {
            MinecraftServer.getCommandManager().register(DeleteCommand())
            MinecraftServer.getCommandManager().register(SaveCommand())
        }

        ServerMode.TESTING -> {
            MinecraftServer.getCommandManager().register(PackCommand())
            MinecraftServer.getCommandManager().register(GamemodeCommand())
            MinecraftServer.getCommandManager().register(StartCommand())
        }

        else -> {}
    }

    loadAnimations()

    val mission = MissionBuilder()
        .useMap(TestMap())
        .build()
    mission.prep()
    Global.world = mission.world
    Global.mission = mission

    val globalEventHandler = MinecraftServer.getGlobalEventHandler();
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        if (mission.hasStarted) {
            event.player.kick(Component.text("This server is currently busy."))
        }
        event.player.respawnPoint = Pos(44.0, -60.0, 7.0)
        event.player.sendResourcePacks(getRequest())
        mission.world.addPlayableCharacter(
            PlayableCharacter(
                mission.world,
                event.player,
                event.player.username
            )
        )
        event.spawningInstance = mission.world.instanceContainer
    }

    globalEventHandler.addListener<PlayerSkinInitEvent>(
        PlayerSkinInitEvent::class.java
    ) { event: PlayerSkinInitEvent ->
        val texture = getOverlayedSkin(PlayerSkin.fromUuid(event.player.uuid.toString())!!, Overlays.GUARD)
        val skin = PlayerSkin(
            texture?.value,
            texture?.signature
        )
        event.skin = skin
    }

    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if (mission.hasStarted) return@addListener
        event.player.showTitle(
            Title.title(
                Component.text(Glyphs.BLACK),
                Component.empty(),
                Title.Times.times(
                    Duration.ofMillis(0),
                    Duration.ofDays(9999),
                    Duration.ofMillis(0)
                )
            )
        )
        if (Global.serverMode == ServerMode.EDITOR) {
            event.player.giveEditorInventory()
            event.player.setGameMode(GameMode.CREATIVE)
            event.player.sendMessage("Welcome to the editor!")
            event.player.flyingSpeed = 0.2f
        }
        event.player.hideFromTab()
        val team = TeamBuilder("player", MinecraftServer.getTeamManager())
            .teamColor(NamedTextColor.GOLD)
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build()
        event.player.team = team
        event.player.isGlowing = true
        event.player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).baseValue = 0.0
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
        if (mission.hasStarted) {
            mission.removePlayer(event.player)
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

    globalEventHandler.addListener(PlayerSwapItemEvent::class.java) { event ->
        event.player.instinctPing()
        event.isCancelled = true
    }

    thread {
        while (true) {
            instinctPingTask()
            Thread.sleep(100)
        }
    }

    minecraftServer.start("0.0.0.0", 25565)
}