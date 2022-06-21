package com.k_antonov.jokes.data

import com.k_antonov.jokes.ui.JokeUiEntity

interface JokeStatusChanger {

    suspend fun addOrRemove(id: Int, joke: Joke): JokeUiEntity
}