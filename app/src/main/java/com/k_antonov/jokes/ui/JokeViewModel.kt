package com.k_antonov.jokes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k_antonov.jokes.data.Model
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JokeViewModel(
    private val model: Model,
    private val liveDataWrapper: LiveDataWrapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    fun getJoke() = viewModelScope.launch {
        model.getJoke().passData(liveDataWrapper)
    }

    fun changeJokeStatus() = viewModelScope.launch(dispatcher) {
        model.changeJokeStatus()?.passData(liveDataWrapper)
    }

    fun chooseFavorites(favorites: Boolean) = model.chooseDataSource(favorites)

    fun observe(owner: LifecycleOwner, observer: Observer<Pair<String, Int>>) =
        liveDataWrapper.observe(owner, observer)
}