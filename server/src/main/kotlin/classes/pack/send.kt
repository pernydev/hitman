package me.perny.hitman.classes.pack

import me.perny.hitman.PACK_URL
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import java.net.URI

fun getRequest(): ResourcePackRequest {
    val packInfo = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create(PACK_URL))
        .hash("")
        .build()

    val request = ResourcePackRequest.resourcePackRequest()
        .packs(packInfo)
        .prompt(Component.text("Tarvitset resurssipaketin palvelimen toimintaa varten."))
        .required(true)
        .build();

    return request
}