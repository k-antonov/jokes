package com.k_antonov.jokes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k_antonov.jokes.data.Model
import kotlinx.coroutines.launch

class JokeViewModel(
    private val model: Model,
    private val liveDataWrapper: LiveDataWrapper
) : ViewModel() {

    fun getJoke() = viewModelScope.launch {
        liveDataWrapper.getData(
            model.getJoke().getData()
        )
    }

    fun changeJokeStatus() = viewModelScope.launch {
        model.changeJokeStatus()?.let {
            liveDataWrapper.getData(it.getData())
        }
    }

    fun chooseFavorites(favorites: Boolean) = model.chooseDataSource(favorites)

    fun observe(owner: LifecycleOwner, observer: Observer<Pair<String, Int>>) =
        liveDataWrapper.observe(owner, observer)
}