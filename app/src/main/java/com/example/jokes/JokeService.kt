package com.example.jokes

import retrofit2.Call
import retrofit2.http.GET

interface JokeService {

    @GET("https://v2.jokeapi.dev/joke/Any?type=twopart")
    fun getJoke(): Call<JokeRemoteEntity>
}
