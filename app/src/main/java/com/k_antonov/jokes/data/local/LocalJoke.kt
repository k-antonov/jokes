package com.k_antonov.jokes.data.local

import com.k_antonov.jokes.data.Joke
import com.k_antonov.jokes.data.JokeChanger
import com.k_antonov.jokes.data.JokeStatusChanger
import com.k_antonov.jokes.ui.JokeUiEntity

interface LocalJoke : JokeChanger {

    fun save(joke: Joke)

    fun clear()

    class Base : LocalJoke {

        private var local: Joke? = null

        override suspend fun changeStatus(jokeStatusChanger: JokeStatusChanger): JokeUiEntity? =
            local?.changeStatus(jokeStatusChanger)

        override fun save(joke: Joke) {
            local = joke
        }

        override fun clear() {
            local = null
        }
    }
}