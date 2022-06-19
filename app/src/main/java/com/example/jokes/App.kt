package com.example.jokes

import android.app.Application

class App : Application() {

    lateinit var viewModel: ViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModel(
            Model.Base(
                JokeService.Base(),
                ResourceManager.Base(this)
            )
        )
    }
}