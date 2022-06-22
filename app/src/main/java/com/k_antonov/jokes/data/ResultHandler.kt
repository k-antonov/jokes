package com.k_antonov.jokes.data

import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.Result

abstract class ResultHandler<S, E>(private val dataSource: DataSource<S, E>) {

    suspend fun process(): JokeUiEntity = handleResult(dataSource.getJoke())

    protected abstract fun handleResult(result: Result<S, E>): JokeUiEntity

}