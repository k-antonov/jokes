package com.k_antonov.jokes.data.remote

import android.util.Log
import com.k_antonov.jokes.data.JokeDataFetcher
import com.k_antonov.jokes.utils.Result
import java.net.UnknownHostException

interface RemoteDataSource : JokeDataFetcher<JokeRemoteEntity, ErrorType> {

    class Base(private val service: JokeService) : RemoteDataSource {
        override suspend fun getJoke(): Result<JokeRemoteEntity, ErrorType> {
            return try {
//                val jokeRemoteEntity = service.getJoke().execute().body()!!
                val jokeRemoteEntity = service.getJoke()
                Log.d("RemoteDataSource", "currentThread: ${Thread.currentThread().name}")
                Result.Success(jokeRemoteEntity)
            } catch (e: Exception) {
                val errorType = if (e is UnknownHostException)
                    ErrorType.NO_CONNECTION
                else
                    ErrorType.SERVICE_UNAVAILABLE
                Result.Error(errorType)
            }
        }
    }
}

enum class ErrorType {
    NO_CONNECTION,
    SERVICE_UNAVAILABLE
}