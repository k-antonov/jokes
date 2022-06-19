package com.example.jokes

import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

interface JokeService {

    fun getJoke(callback: ServiceCallback)

    class Base : JokeService {
        override fun getJoke(callback: ServiceCallback) {
            Thread {
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(JOKE_URL)
                    connection = url.openConnection() as HttpURLConnection
                    InputStreamReader(BufferedInputStream(connection.inputStream)).use {
                        val line: String = it.readText()
                        callback.returnSuccess(line)
                    }
                } catch (e: Exception) {
                    if (e is UnknownHostException)
                        callback.returnError(ErrorType.NO_CONNECTION)
                    else
                        callback.returnError(ErrorType.OTHER)
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

        private companion object {
            const val JOKE_URL = "https://v2.jokeapi.dev/joke/Any?type=twopart"
        }
    }
}

interface ServiceCallback {

    fun returnSuccess(data: String)

    fun returnError(errorType: ErrorType)
}

enum class ErrorType {
    NO_CONNECTION,
    OTHER
}