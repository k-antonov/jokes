package com.k_antonov.jokes.data.remote

import com.k_antonov.jokes.data.DataSource
import com.k_antonov.jokes.data.JokeError
import com.k_antonov.jokes.data.ResultHandler
import com.k_antonov.jokes.data.local.LocalJoke
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.Result

class RemoteResultHandler(
    private val localJoke: LocalJoke,
    dataSource: DataSource<JokeRemoteEntity, ErrorType>,
    private val noConnection: JokeError.NoConnection,
    private val serviceUnavailable: JokeError.ServiceUnavailable
) : ResultHandler<JokeRemoteEntity, ErrorType>(dataSource) {

    override fun handleResult(result: Result<JokeRemoteEntity, ErrorType>): JokeUiEntity =
        when (result) {
            is Result.Success<JokeRemoteEntity> -> {
                result.data.toJoke().let {
                    localJoke.save(it)
                    it.toJokeUiBase()
                }
            }
            is Result.Error<ErrorType> -> {
                localJoke.clear()
                val failure = if (result.exception == ErrorType.SERVICE_UNAVAILABLE)
                    serviceUnavailable
                else
                    noConnection
                JokeUiEntity.Failed(failure.getMessage())
            }
        }
}