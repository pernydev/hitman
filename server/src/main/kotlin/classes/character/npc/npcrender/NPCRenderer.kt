package me.perny.hitman.classes.character.npc.npcrender

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.perny.hitman.classes.character.npc.NPCCharacter
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.*
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.metadata.display.ItemDisplayMeta
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.worldseed.multipart.animations.AnimationHandler
import net.worldseed.multipart.animations.AnimationHandlerImpl
import net.worldseed.multipart.events.ModelDamageEvent
import kotlin.concurrent.thread


class NPCRenderer(
    val npcCharacter: NPCCharacter,
) : EntityCreature(EntityType.ZOMBIE) {
    private val model: NPCModel
    protected val animationHandler: AnimationHandler
    private var emoteIndex = 0
    private var pathfindingPath = listOf<Pos>()
    private var pathfindingIndex = 0
    private var pathfindingFinishedRunnable: Runnable? = null

    init {
        val self: Entity = this
        this.model = object : NPCModel(npcCharacter.skin.getPlayerSkin()) {
            override fun setPosition(pos: Pos) {
                super.setPosition(pos)
                if (self.instance != null) {
                    self.teleport(pos)
                }
            }
        }
        model.init(npcCharacter.world.instanceContainer, npcCharacter.position)
        this.setBoundingBox(0.8, 1.8, 0.8)
        this.isInvisible = true
        getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = 0.0010000000474974513
        setInstance(npcCharacter.world.instanceContainer, npcCharacter.position).join()
        this.animationHandler = object : AnimationHandlerImpl(this.model) {
            override fun loadDefaultAnimations() {
            }
        }
        eventNode().addListener(
            EntityDamageEvent::class.java
        ) { event: EntityDamageEvent ->
            event.isCancelled = true
            val modelDamageEvent = ModelDamageEvent(this.model, event)
            EventDispatcher.call(modelDamageEvent)
        }.addListener(
            PlayerEntityInteractEvent::class.java
        ) { event: PlayerEntityInteractEvent ->
            val modelInteractEvent = NPCModelInteractEvent(this.model, event)
            EventDispatcher.call(modelInteractEvent)
        }
        model.draw()
        model.draw()
        loadEmotes(NPC_ANIMATIONS)
    }

    fun loadEmotes(emotes: Map<String, JsonObject>) {
        val var2: Iterator<*> = emotes.entries.iterator()
        while (var2.hasNext()) {
            val entry: Map.Entry<String, JsonObject> = var2.next() as Map.Entry<String, JsonObject>
            animationHandler.registerAnimation(
                entry.key, entry.value as JsonElement, this.emoteIndex
            )
            ++this.emoteIndex
        }
    }

    fun playPath(path: List<Pos>) {
        pathfindingPath = path
        pathfindingIndex = 0

        if (pathfindingPath.size == 0) {
            pathfindingFinishedRunnable?.run()
            return
        }

        setRotation(position.withLookAt(pathfindingPath[pathfindingIndex]).yaw)
        position = pathfindingPath[pathfindingIndex]
        teleport(position)
        playPathAnimation()
    }

    fun playPath(path: List<Pos>, runnable: Runnable) {
        pathfindingFinishedRunnable = runnable
        playPath(path)
    }

    fun walkingFinish() {
        var duration = 0
        model.parts.forEach {
            val meta = it.entity.entityMeta as ItemDisplayMeta
            duration = meta.posRotInterpolationDuration
            meta.posRotInterpolationDuration = 0
        }
        teleport(pathfindingPath[pathfindingIndex].add(0.0, 0.01, 0.0))
        pathfindingIndex++
        // if not last frame
        if (pathfindingIndex != pathfindingPath.size - 1) {
            setRotation(position.withLookAt(pathfindingPath[pathfindingIndex + 1]).yaw)
        }
        model.parts.forEach {
            val meta = it.entity.entityMeta as ItemDisplayMeta
            meta.posRotInterpolationDuration = duration
        }
        MinecraftServer.getSchedulerManager().scheduleNextTick {
            playPathAnimation()
        }
    }

    fun playPathAnimation() {
        thread {
            if (pathfindingIndex == pathfindingPath.size - 1) {
                pathfindingFinishedRunnable?.run()
                return@thread
            }

            when (instance.getBlock(pathfindingPath[pathfindingIndex].add(0.0, -1.0, 0.0)).name()) {
                "minecraft:oak_stairs" -> {
                    animationHandler.playOnce("walking.stairs", { walkingFinish() })
                    return@thread
                }
            }

            // if the next position has an X and a Z difference
            if (pathfindingPath[pathfindingIndex].x != pathfindingPath[pathfindingIndex + 1].x && pathfindingPath[pathfindingIndex].z != pathfindingPath[pathfindingIndex + 1].z) {
                animationHandler.playOnce("walking.diagonal.continue", { walkingFinish() })
                return@thread
            }

            animationHandler.playOnce("walking.continue", { walkingFinish() })
        }
    }

    override fun remove() {
        model.destroy()
        animationHandler.destroy()
        super.remove()
    }

    override fun tick(time: Long) {
        val position = this.getPosition()
        super.tick(time)
        if (position != this.getPosition()) {
            model.position = this.getPosition()
        }
        npcCharacter.position = this.getPosition()
    }

    fun setRotation(yaw: Float) {
        model.globalRotation = yaw.toDouble()
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        model.addViewer(player)
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        model.removeViewer(player)
    }
}
