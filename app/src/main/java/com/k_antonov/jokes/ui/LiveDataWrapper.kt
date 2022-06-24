package com.k_antonov.jokes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface LiveDataWrapper {

    fun setData(uiState: JokeViewModel.UiState)

    fun observe(owner: LifecycleOwner, observer: Observer<JokeViewModel.UiState>)

    class Base : LiveDataWrapper {

        private val liveData = MutableLiveData<JokeViewModel.UiState>()

        override fun setData(uiState: JokeViewModel.UiState) {
            liveData.value = uiState
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<JokeViewModel.UiState>) {
            liveData.observe(owner, observer)
        }
    }
}