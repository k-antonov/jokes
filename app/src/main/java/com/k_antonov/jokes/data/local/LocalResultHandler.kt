package com.k_antonov.jokes.data.local

import com.k_antonov.jokes.data.DataSource
import com.k_antonov.jokes.data.Joke
import com.k_antonov.jokes.data.JokeError
import com.k_antonov.jokes.data.ResultHandler
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.Result

class LocalResultHandler(
    private val localJoke: LocalJoke,
    dataSource: DataSource<Joke, Unit>,
    private val noCachedJokes: JokeError.NoCachedJokes
) : ResultHandler<Joke, Unit>(dataSource) {

    override fun handleResult(result: Result<Joke, Unit>): JokeUiEntity =
        when (result) {
            is Result.Success<Joke> -> result.data.let {
                localJoke.save(it)
                it.toJokeUiFavorite()
            }
            is Result.Error -> {
                localJoke.clear()
                JokeUiEntity.Failed(noCachedJokes.getMessage())
            }
        }
}