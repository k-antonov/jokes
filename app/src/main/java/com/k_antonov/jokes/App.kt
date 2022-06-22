package com.k_antonov.jokes

import android.app.Application
import com.k_antonov.jokes.data.*
import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.local.LocalJoke
import com.k_antonov.jokes.data.local.LocalResultHandler
import com.k_antonov.jokes.data.local.RealmProvider
import com.k_antonov.jokes.data.remote.JokeService
import com.k_antonov.jokes.data.remote.RemoteDataSource
import com.k_antonov.jokes.data.remote.RemoteResultHandler
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

        val localJoke = LocalJoke.Base()
        val localDataSource = LocalDataSource.Base(
            RealmProvider.Base()
        )
        val resourceManager = ResourceManager.Base(this)

        jokeViewModel = JokeViewModel(
            Model.Base(
                localDataSource = localDataSource,
                localResultHandler = LocalResultHandler(
                    localJoke = localJoke,
                    dataSource = localDataSource,
                    noCachedJokes = JokeError.NoCachedJokes(resourceManager)
                ),
                remoteResultHandler = RemoteResultHandler(
                    localJoke = localJoke,
                    dataSource = RemoteDataSource.Base(retrofit.create(JokeService::class.java)),
                    noConnection = JokeError.NoConnection(resourceManager),
                    serviceUnavailable = JokeError.ServiceUnavailable(resourceManager)
                ),
                localJoke = localJoke
            )
        )
    }
}