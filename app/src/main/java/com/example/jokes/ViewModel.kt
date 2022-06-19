package com.example.jokes

import androidx.annotation.DrawableRes

class ViewModel(private val model: Model) {

    private var callback: DataCallback? = null

    private val jokeCallback = object : JokeCallback {
        override fun provide(joke: Joke) {
            callback?.let {
                joke.map(it)
            }
        }
    }

    fun init(callback: DataCallback) {
        this.callback = callback
        model.init(jokeCallback)
    }

    fun getJoke() {
        model.getJoke()
    }

    fun clear() {
        callback = null
        model.clear()
    }

    fun changeJokeStatus() {
        model.changeJokeStatus(jokeCallback)
    }

    fun chooseFavorites(favorites: Boolean) {
        model.chooseDataSource(favorites)
    }

}

interface DataCallback {

    fun provideText(text: String)

    fun provideIconRes(@DrawableRes id: Int)
}