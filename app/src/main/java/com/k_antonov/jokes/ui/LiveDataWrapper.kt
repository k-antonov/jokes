package com.k_antonov.jokes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface LiveDataWrapper {

    fun getData(data: Pair<String, Int>)

    fun observe(owner: LifecycleOwner, observer: Observer<Pair<String, Int>>)

    class Base : LiveDataWrapper {

        private val liveData = MutableLiveData<Pair<String, Int>>()

        override fun getData(data: Pair<String, Int>) {
            liveData.value = data
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<Pair<String, Int>>) {
            liveData.observe(owner, observer)
        }
    }
}