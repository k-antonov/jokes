package com.k_antonov.jokes.utils

sealed class Result<out R, out E> {
    data class Success<out T>(val data: T): Result<T, Nothing>()
    data class Error<out S>(val exception: S): Result<Nothing, S>()
}