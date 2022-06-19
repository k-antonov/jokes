package com.example.jokes

interface Model {

    fun getJoke()

    fun init(callback: ResultCallback)

    fun clear()
}

interface ResultCallback {

    fun provideSuccess(joke: Joke)

    fun provideError(error: JokeError)
}