package me.perny.hitman.classes.character.signal

interface SignalListener {
    fun signal(signal: Signal)

    fun investigate(signal: Signal)
    fun help(signal: Signal)
    fun danger(signal: Signal)
}
