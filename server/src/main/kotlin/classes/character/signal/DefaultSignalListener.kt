package me.perny.hitman.classes.character.signal

class DefaultSignalListener : SignalListener {
    override fun signal(signal: Signal) {
        when (signal.channel) {
            SignalChannel.INVESTIGATE -> investigate(signal)
            SignalChannel.HELP -> help(signal)
            SignalChannel.DANGER -> danger(signal)
        }
    }

    override fun investigate(signal: Signal) {
        TODO("Not yet implemented")
    }

    override fun help(signal: Signal) {
        TODO("Not yet implemented")
    }

    override fun danger(signal: Signal) {
        TODO("Not yet implemented")
    }
}