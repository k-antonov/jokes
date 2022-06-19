package com.example.jokes

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    lateinit var viewModel: ViewModel

    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.google.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        viewModel = ViewModel(
            Model.Base(
                RemoteDataSource.Base(retrofit.create(JokeService::class.java)),
                LocalDataSource.Test(),
                ResourceManager.Base(this)
            )
        )
    }
}