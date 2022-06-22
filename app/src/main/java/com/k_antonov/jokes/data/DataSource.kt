package com.k_antonov.jokes.data

import com.k_antonov.jokes.utils.Result

interface DataSource<S, E> {
    suspend fun getJoke(): Result<S, E>
}