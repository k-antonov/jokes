package com.example.jokes

import retrofit2.Call
import retrofit2.Response
import java.net.UnknownHostException

interface RemoteDataSource {

    fun getJoke(callback: JokeRemoteCallback)

    class Base(private val service: JokeService) : RemoteDataSource {
        override fun getJoke(callback: JokeRemoteCallback) {
            service.getJoke().enqueue(object : retrofit2.Callback<JokeRemoteEntity> {
                override fun onResponse(
                    call: Call<JokeRemoteEntity>,
                    response: Response<JokeRemoteEntity>
                ) {
                    if (response.isSuccessful) {
                        callback.provide(response.body()!!)
                    } else {
                        callback.fail(ErrorType.SERVICE_UNAVAILABLE)
                    }
                }

                override fun onFailure(call: Call<JokeRemoteEntity>, t: Throwable) {
                    if (t is UnknownHostException) {
                        callback.fail(ErrorType.NO_CONNECTION)
                    } else {
                        callback.fail(ErrorType.SERVICE_UNAVAILABLE)
                    }
                }
            })
        }
    }

    class Test : RemoteDataSource {
        private var count = 0
        override fun getJoke(callback: JokeRemoteCallback) {
            callback.provide(
                JokeRemoteEntity(
                error = false,
                category = "test category",
                type = "test type",
                setup = "test setup $count",
                delivery = "test punchline $count",
                id = 0,
                safe = true,
                lang = "test lang",
                flags = JokeRemoteEntity.Flags(
                    nsfw = false,
                    religious = false,
                    political = false,
                    racist = false,
                    sexist = false,
                    explicit = false
                )
            ))
            count++
        }
    }

}

interface JokeRemoteCallback {

    fun provide(jokeRemoteEntity: JokeRemoteEntity)

    fun fail(errorType: ErrorType)
}

enum class ErrorType {
    NO_CONNECTION,
    SERVICE_UNAVAILABLE
}