package com.example.jokes

import retrofit2.Call
import retrofit2.Response
import java.net.UnknownHostException

interface RemoteDataSource {

    fun getJoke(jokeRemoteCallback: JokeRemoteCallback)

    class Base(private val service: JokeService) : RemoteDataSource {
        override fun getJoke(jokeRemoteCallback: JokeRemoteCallback) {
            service.getJoke().enqueue(object : retrofit2.Callback<JokeRemoteEntity> {
                override fun onResponse(
                    call: Call<JokeRemoteEntity>,
                    response: Response<JokeRemoteEntity>
                ) {
                    if (response.isSuccessful) {
                        jokeRemoteCallback.provide(response.body()!!.toJoke())
                    } else {
                        jokeRemoteCallback.fail(ErrorType.SERVICE_UNAVAILABLE)
                    }
                }

                override fun onFailure(call: Call<JokeRemoteEntity>, t: Throwable) {
                    val errorType = if (t is UnknownHostException)
                        ErrorType.NO_CONNECTION
                    else
                        ErrorType.SERVICE_UNAVAILABLE
                    jokeRemoteCallback.fail(errorType)
                }
            })
        }
    }

    class Test : RemoteDataSource {
        private var count = 0

        override fun getJoke(jokeRemoteCallback: JokeRemoteCallback) {
            jokeRemoteCallback.provide(
                Joke(
                    error = false,
                    category = "test category",
                    type = "test type",
                    setup = "test setup $count",
                    delivery = "test delivery $count",
                    id = 0,
                    safe = false,
                    lang = "test lang",
                    flags = Joke.Flags(
                        nsfw = false,
                        religious = false,
                        political = false,
                        racist = false,
                        sexist = false,
                        explicit = false
                    )
                )
            )
            count++
        }
    }

}

interface JokeRemoteCallback {

    fun provide(joke: Joke)

    fun fail(errorType: ErrorType)
}

enum class ErrorType {
    NO_CONNECTION,
    SERVICE_UNAVAILABLE
}