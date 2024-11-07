package me.perny.hitman.classes.character.speak

val conversations = mapOf(
    "politics.1" to 3
)

val singleLinePool = mapOf(
    "distanceAnnoyed" to listOf(
        "lines.distanceAnnoyed.1",
        "lines.distanceAnnoyed.2",
        "lines.distanceAnnoyed.3"
    )
)

fun getSingleLine(pool: String): String {
    return singleLinePool[pool]?.random() ?: throw IllegalArgumentException("Pool $pool is empty")
}

val pools = mapOf(
    Pool.OFF_TOPIC to listOf(
        "politics.1"
    )
)

enum class Pool {
    OFF_TOPIC
}