package me.perny.hitman.classes.character.npc.npcrender

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.instance.Instance
import net.worldseed.gestures.ModelBoneEmote
import net.worldseed.multipart.GenericModelImpl
import net.worldseed.multipart.model_bones.ModelBone
import net.worldseed.multipart.model_bones.ModelBoneImpl
import net.worldseed.multipart.model_bones.ModelBoneViewable
import java.io.StringReader
import java.util.function.Function
import java.util.function.Predicate

open class NPCModel(private val skin: PlayerSkin) : GenericModelImpl() {
    override fun registerBoneSuppliers() {
        boneSuppliers[Predicate { name: String? -> true }] =
            Function { info: ModelBoneInfo ->
                ModelBoneEmote(
                    info.pivot(), info.name(), info.rotation(), info.model(),
                    BONE_TRANSLATIONS[info.name()]!!,
                    VERTICAL_OFFSETS.getOrDefault(info.name(), 0.0),
                    this.skin
                )
            }
    }

    override fun addViewer(player: Player): Boolean {
        println("Adding viewer " + player.username)
        return super.addViewer(player)
    }

    override fun removeViewer(player: Player): Boolean {
        println("Removing viewer " + player.username)
        return super.removeViewer(player)
    }

    override fun getId(): String {
        return ""
    }

    private fun init_(instance: Instance?, position: Pos) {
        this.instance = instance
        this.position = position
        this.globalRotation = position.yaw().toDouble()

        try {
            super.loadBones(MODEL_JSON, 1.0f)
        } catch (var5: Exception) {
            val e = var5
            e.printStackTrace()
        }

        var modelBonePart: ModelBone
        val var6: Iterator<*> = parts.values.iterator()
        while (var6.hasNext()) {
            modelBonePart = var6.next() as ModelBone
            if (modelBonePart is ModelBoneViewable) {
                viewableBones.add(modelBonePart as ModelBoneImpl)
            }
            modelBonePart.spawn(instance, modelBonePart.calculatePosition()).join()
        }

        this.draw()
    }

    override fun init(instance: Instance?, position: Pos) {
        this.init_(instance, position)
    }

    override fun getDiff(boneName: String): Point {
        return BONE_DIFFS.getOrDefault(boneName, Pos(0.0, 0.0, 0.0))
    }

    override fun setGlobalScale(scale: Float) {
    }

    override fun getOffset(boneName: String): Point {
        return BONE_OFFSETS.getOrDefault(boneName, Vec.ZERO)
    }

    companion object {
        protected val GSON: Gson = (GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create()
        private val MODEL_JSON = GSON.fromJson(
            StringReader(
                "{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n\t\t\t\t\"identifier\": \"geometry.unknown\",\n\t\t\t\t\"texture_width\": 64,\n\t\t\t\t\"texture_height\": 64,\n\t\t\t\t\"visible_bounds_width\": 3,\n\t\t\t\t\"visible_bounds_height\": 3.5,\n\t\t\t\t\"visible_bounds_offset\": [0, 1.25, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"Head\",\n\t\t\t\t\t\"pivot\": [0, 24, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"uv\": [0, 0]},\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"inflate\": 0.5, \"uv\": [32, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"Body\",\n\t\t\t\t\t\"pivot\": [0, 24, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"uv\": [16, 16]},\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"inflate\": 0.25, \"uv\": [16, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"RightArm\",\n\t\t\t\t\t\"pivot\": [-5, 22, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"uv\": [40, 16]},\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [40, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"LeftArm\",\n\t\t\t\t\t\"pivot\": [5, 22, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"uv\": [32, 48]},\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [48, 48]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"RightLeg\",\n\t\t\t\t\t\"pivot\": [-1.9, 12, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"uv\": [0, 16]},\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"LeftLeg\",\n\t\t\t\t\t\"pivot\": [1.9, 12, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"uv\": [16, 48]},\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 48]}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}"
            ),
            JsonObject::class.java
        ) as JsonObject

        val BONE_OFFSETS: Map<String, Point> = java.util.Map.ofEntries<String, Point>(
            java.util.Map.entry("Head", Vec(0.0, 0.0, 0.0)),
            java.util.Map.entry("RightArm", Vec(0.0, 0.0, 0.0)),
            java.util.Map.entry("LeftArm", Vec(0.0, 0.0, 0.0)),
            java.util.Map.entry("Body", Vec(0.0, 0.0, 0.0)),
            java.util.Map.entry("RightLeg", Vec(0.0, 0.0, 0.0)),
            java.util.Map.entry("LeftLeg", Vec(-0.0, 0.0, 0.0))
        )

        private val VERTICAL_OFFSETS: Map<String, Double> =
            java.util.Map.of("Head", 1.4, "RightArm", 1.4, "LeftArm", 1.4, "Body", 1.4, "RightLeg", 0.7, "LeftLeg", 0.7)
        private val BONE_TRANSLATIONS: Map<String, Int> = java.util.Map.of(
            "Head",
            0,
            "RightArm",
            -1024,
            "LeftArm",
            -2048,
            "Body",
            -3072,
            "RightLeg",
            -4096,
            "LeftLeg",
            -5120
        )
        private val BONE_DIFFS: Map<String, Point> = java.util.Map.ofEntries()
    }
}
