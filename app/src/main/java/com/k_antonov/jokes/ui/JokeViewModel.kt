package com.k_antonov.jokes.ui

import androidx.annotation.DrawableRes
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
        liveDataWrapper.setData(UiState.Progress)
        model.getJoke().passData(liveDataWrapper)
    }

    fun changeJokeStatus() = viewModelScope.launch(dispatcher) {
        model.changeJokeStatus()?.passData(liveDataWrapper)
    }

    fun chooseFavorites(favorites: Boolean) = model.chooseDataSource(favorites)

    fun observe(owner: LifecycleOwner, observer: Observer<UiState>) =
        liveDataWrapper.observe(owner, observer)

    sealed class UiState {
        object Progress : UiState()
        class Loaded(val text: String, @DrawableRes val iconResId: Int) : UiState()
    }
}