package com.example.jokes

interface JokeLocalCallback {

    fun provide(joke: Joke)

    fun fail()
}