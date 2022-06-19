package com.example.jokes

class Joke(private val setup: String, private val punchline: String) {

    fun getJokeUi() = "$setup\n$punchline"
}