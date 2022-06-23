package com.k_antonov.jokes.data

import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.local.LocalJoke
import com.k_antonov.jokes.data.remote.ErrorType
import com.k_antonov.jokes.data.remote.JokeRemoteEntity
import com.k_antonov.jokes.ui.JokeUiEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface Model {

    suspend fun getJoke(): JokeUiEntity
    suspend fun changeJokeStatus(): JokeUiEntity?
    fun chooseDataSource(local: Boolean)

    class Base(
        private val localDataSource: LocalDataSource,
        private val localResultHandler: ResultHandler<Joke, Unit>,
        private val remoteResultHandler: ResultHandler<JokeRemoteEntity, ErrorType>,
        private val localJoke: LocalJoke
    ) : Model {

        private var currentResultHandler: ResultHandler<*, *> = remoteResultHandler

        override suspend fun getJoke(): JokeUiEntity = withContext(Dispatchers.IO) {
            return@withContext currentResultHandler.process()
        }

        override suspend fun changeJokeStatus(): JokeUiEntity? = withContext(Dispatchers.IO) {
            return@withContext localJoke.changeStatus(localDataSource)
        }

        override fun chooseDataSource(local: Boolean) {
            currentResultHandler = if (local) localResultHandler else remoteResultHandler
        }
    }

}