package com.k_antonov.jokes.data

import com.k_antonov.jokes.ui.JokeUiEntity

interface JokeChanger {

    suspend fun changeStatus(jokeStatusChanger: JokeStatusChanger): JokeUiEntity?
}