package com.example.jokes

interface JokeLocalCallback {

    fun provide(jokeRemoteEntity: JokeRemoteEntity)

    fun fail()
}