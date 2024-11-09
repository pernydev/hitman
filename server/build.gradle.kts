plugins {
    kotlin("jvm") version "2.0.0"
}

group = "me.perny.hitman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://reposilite.worldseed.online/public")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.minestom:minestom-snapshots:dba90a461b")
    implementation("com.github.mworzala.mc_debug_renderer:minestom:60b94941e9")
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.2.2")
    implementation("org.apache.commons:commons-io:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}