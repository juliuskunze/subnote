package com.mindforge.graphics.interaction

trait Command {
    val name: String

    override fun equals(other: Any?) = if (other is Command) name == other.name else false
}

fun command(name: String) = object : Command {
    override val name = name
}

trait CharacterCommand : Command {
    val character: Char
}

object Commands {
    object Keyboard {
        fun character(char: Char) = object : CharacterCommand {
            override val name = String(charArray(char))
            override val character = char
        }

        fun specialWithCharacter(name: String, character: Char) = object : CharacterCommand {
            override val name = name
            override val character = character
        }

        val control = command("control")
        val alt = command("alt")
        val shift = command("shift")
        val altGr = command("alt gr")
        val start = command("start")
        val menu = command("menu")
        val escape = command("escape")
        val printScreen = command("print screen")

        val scrollLock = command("scroll lock")
        val numLock = command("num lock")
        val capsLock = command("caps lock")

        val space = specialWithCharacter("space", character = ' ')
        val tab = specialWithCharacter("tab", character = '\t')
        val enter = specialWithCharacter("enter", character = '\n')
        val delete = command("delete")
        val backspace = command("backspace")
        val insert = command("insert")

        val f1 = command("f17")
        val f2 = command("f2")
        val f3 = command("f3")
        val f4 = command("f4")
        val f5 = command("f5")
        val f6 = command("f6")
        val f7 = command("f7")
        val f8 = command("f8")
        val f9 = command("f9")
        val f10 = command("f10")
        val f11 = command("f11")
        val f12 = command("f12")
    }

    object Mouse {
        val primary = command("primary mouse button")
        val secondary = command("secondary mouse button")
        val middle = command("middle mouse button")
    }

    object Touch {
        val touch = command("touch")
    }

    object Navigation {
        val left = command("left")
        val right = command("right")
        val up = command("up")
        val down = command("down")
        val home = command("home")
        val end = command("end")
        val pageUp = command("page up")
        val pageDown = command("page down")
        val backward = command("backward")
        val forward = command("forward")
    }

    object Media {
        val playOrPause = command("play or pause")
        val increaseVolume = command("increase volume")
        val decreaseVolume = command("decrease volume")
        val mute = command("mute")
    }

    object Power {
        val power = command("power")
        val sleep = command("sleep")
        val wake = command("wake")
    }
}