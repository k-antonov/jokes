package com.example.jokes

import androidx.annotation.DrawableRes

class ViewModel(private val model: Model) {

    private var dataCallback: DataCallback? = null

    private val jokeCallback = object : JokeCallback {
        override fun provide(jokeUiEntity: JokeUiEntity) {
            dataCallback?.let {
                jokeUiEntity.map(it)
            }
        }
    }

    fun init(callback: DataCallback) {
        this.dataCallback = callback
        model.init(jokeCallback)
    }

    fun getJoke() {
        model.getJoke()
    }

    fun clear() {
        dataCallback = null
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