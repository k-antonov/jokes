package com.k_antonov.jokes.ui

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k_antonov.jokes.data.Model
import kotlinx.coroutines.launch

class JokeViewModel(private val model: Model) : ViewModel() {

    private var dataCallback: DataCallback? = null

    fun init(callback: DataCallback) {
        this.dataCallback = callback
    }

    fun getJoke() = viewModelScope.launch {
        val jokeUiEntity = model.getJoke()
        dataCallback?.let {
            jokeUiEntity.map(it)
        }
    }

    fun clear() {
        dataCallback = null
    }

    fun changeJokeStatus() = viewModelScope.launch {
        val jokeUiEntity = model.changeJokeStatus()
        dataCallback?.let {
            jokeUiEntity?.map(it)
        }
    }

    fun chooseFavorites(favorites: Boolean) {
        model.chooseDataSource(favorites)
    }

}

interface DataCallback {

    fun provideText(text: String)

    fun provideIconRes(@DrawableRes id: Int)
}