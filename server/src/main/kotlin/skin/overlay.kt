package me.perny.hitman.skin

import com.google.gson.Gson
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

val client = OkHttpClient()

fun getOverlayedSkin(playerSkin: PlayerSkin, overlay: Overlays): ApiResponse.ApiResponseData.ApiResponseTexture? {
    val originalSkin = getSkinTexture(playerSkin)
    try {
        // Create form body
        val formBody = FormBody.Builder()
            .add("base", originalSkin)
            .add("overlay", overlay.overlay)
            .build()

        // Build request
        val request = Request.Builder()
            .url("http://localhost:8080/api")
            .post(formBody)
            .build()

        // Execute request
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val responseBody = response.body?.string()
                throw IOException("Unexpected response code: ${response.code}, $responseBody")
            }

            // Parse JSON response
            val responseBody = response.body?.string()
            val apiResponse = Gson().fromJson(responseBody, ApiResponse::class.java)
            return apiResponse.data.texture
        }
    } catch (e: Exception) {
        println("Error sending request: ${e.message}")
        return null
    }
}

fun getSkinTexture(playerSkin: PlayerSkin): String {
    val texture = Base64.getDecoder().decode(playerSkin.textures()).toString(Charsets.UTF_8)
    println("Texture: $texture")
    val skinData = Gson().fromJson(texture, MinecraftSkinData::class.java)
    return skinData.textures.SKIN.url
}

data class MinecraftSkinData(
    val textures: MinecraftSkinTextures
) {
    data class MinecraftSkinTextures(
        val SKIN: MinecraftSkinTexture
    ) {
        data class MinecraftSkinTexture(
            val url: String
        )
    }
}

data class ApiResponse(
    val data: ApiResponseData
) {
    data class ApiResponseData(
        val texture: ApiResponseTexture
    ) {
        data class ApiResponseTexture(
            val value: String,
            val signature: String
        ) {
            fun toSkin(): PlayerSkin {
                return PlayerSkin(value, signature)
            }
        }
    }
}