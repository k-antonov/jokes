package com.example.jokes

import androidx.annotation.DrawableRes

abstract class Joke(private val setup: String, private val punchline: String) {

    private fun getJokeUi() = "$setup\n$punchline"

    @DrawableRes
    protected abstract fun getIconResId(): Int

    fun map(callback: DataCallback) {
        callback.provideText(getJokeUi())
        callback.provideIconRes(getIconResId())
    }

    class Base(setup: String, punchline: String) : Joke(setup, punchline) {
        override fun getIconResId() = R.drawable.ic_unfavorite
    }

    class Favorite(setup: String, punchline: String) : Joke(setup, punchline) {
        override fun getIconResId() = R.drawable.ic_favorite
    }

    class Failed(text: String) : Joke(text, "") {
        override fun getIconResId() = 0
    }
}