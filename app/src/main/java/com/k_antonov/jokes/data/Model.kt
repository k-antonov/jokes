package com.k_antonov.jokes.data

import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.remote.ErrorType
import com.k_antonov.jokes.data.remote.JokeRemoteEntity
import com.k_antonov.jokes.data.remote.RemoteDataSource
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.ResourceManager
import com.k_antonov.jokes.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface Model {

    suspend fun getJoke(): JokeUiEntity
    suspend fun changeJokeStatus(): JokeUiEntity?
    fun chooseDataSource(local: Boolean)

    class Base(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val resourceManager: ResourceManager
    ) : Model {

        private val noConnection by lazy { JokeError.NoConnection(resourceManager) }
        private val serviceUnavailable by lazy { JokeError.ServiceUnavailable(resourceManager) }
        private val noCachedJokes by lazy { JokeError.NoCachedJokes(resourceManager) }

        private var localJoke: Joke? = null

        private var getLocalJoke = false
        override suspend fun getJoke(): JokeUiEntity = withContext(Dispatchers.IO) {
            if (getLocalJoke) {
                return@withContext when (val result = localDataSource.getJoke()) {
                    is Result.Success<Joke> -> result.data.let {
                        localJoke = it
                        it.toJokeUiFavorite()
                    }
                    is Result.Error -> {
                        localJoke = null
                        JokeUiEntity.Failed(noCachedJokes.getMessage())
                    }
                }
            } else {
                return@withContext when (val result = remoteDataSource.getJoke()) {
                    is Result.Success<JokeRemoteEntity> -> {
                        result.data.toJoke().let {
                            localJoke = it
                            it.toJokeUiBase()
                        }
                    }
                    is Result.Error<ErrorType> -> {
                        localJoke = null
                        val failure = if (result.exception == ErrorType.SERVICE_UNAVAILABLE)
                            serviceUnavailable
                        else
                            noConnection
                        JokeUiEntity.Failed(failure.getMessage())
                    }
                }
            }
        }

        override suspend fun changeJokeStatus(): JokeUiEntity? = withContext(Dispatchers.IO) {
            localJoke?.changeStatus(localDataSource)
        }

        override fun chooseDataSource(local: Boolean) {
            getLocalJoke = local
        }
    }

}