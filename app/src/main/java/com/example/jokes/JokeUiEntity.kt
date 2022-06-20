package com.example.jokes

import androidx.annotation.DrawableRes

abstract class JokeUiEntity(private val setup: String, private val delivery: String) {

    private fun getText() = "$setup\n$delivery"

    @DrawableRes
    protected abstract fun getIconResId(): Int

    fun map(dataCallback: DataCallback) {
        dataCallback.provideText(getText())
        dataCallback.provideIconRes(getIconResId())
    }

    class Base(setup: String, delivery: String) : JokeUiEntity(setup, delivery) {
        override fun getIconResId() = R.drawable.ic_unfavorite
    }

    class Favorite(setup: String, delivery: String) : JokeUiEntity(setup, delivery) {
        override fun getIconResId() = R.drawable.ic_favorite
    }

    class Failed(text: String) : JokeUiEntity(text, "") {
        override fun getIconResId() = 0
    }
}