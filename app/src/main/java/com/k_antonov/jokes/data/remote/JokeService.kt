package com.k_antonov.jokes.data.remote

import retrofit2.http.GET

interface JokeService {

    @GET("https://v2.jokeapi.dev/joke/Any?type=twopart")
//    suspend fun getJoke(): Call<JokeRemoteEntity>
    suspend fun getJoke(): JokeRemoteEntity
}
