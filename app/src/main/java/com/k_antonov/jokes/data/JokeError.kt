package com.k_antonov.jokes.data

import com.k_antonov.jokes.R
import com.k_antonov.jokes.utils.ResourceManager

interface JokeError {
    fun getMessage(): String

    class NoConnection(private val resourceManager: ResourceManager) : JokeError {
        override fun getMessage() = resourceManager.getString(R.string.no_connection)
    }

    class ServiceUnavailable(private val resourceManager: ResourceManager) : JokeError {
        override fun getMessage() = resourceManager.getString(R.string.service_unavailable)
    }

    class NoCachedJokes(private val resourceManager: ResourceManager) : JokeError {
        override fun getMessage() = resourceManager.getString(R.string.no_cached_jokes)
    }
}