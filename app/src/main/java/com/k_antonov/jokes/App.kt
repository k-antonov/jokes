package com.k_antonov.jokes

import android.app.Application
import com.k_antonov.jokes.data.*
import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.local.RealmProvider
import com.k_antonov.jokes.data.remote.JokeService
import com.k_antonov.jokes.data.remote.RemoteDataSource
import com.k_antonov.jokes.ui.JokeViewModel
import com.k_antonov.jokes.utils.ResourceManager
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    lateinit var jokeViewModel: JokeViewModel

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.google.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        jokeViewModel = JokeViewModel(
            Model.Base(
                RemoteDataSource.Base(retrofit.create(JokeService::class.java)),
                LocalDataSource.Base(RealmProvider.Base()),
                ResourceManager.Base(this)
            )
        )
    }
}