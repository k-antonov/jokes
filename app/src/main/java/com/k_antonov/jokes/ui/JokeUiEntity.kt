package com.k_antonov.jokes.ui

import androidx.annotation.DrawableRes
import com.k_antonov.jokes.R

abstract class JokeUiEntity(private val setup: String, private val delivery: String) {

    protected open fun getText() = "$setup\n$delivery"

    @DrawableRes
    protected abstract fun getIconResId(): Int

    fun passData(liveDataWrapper: LiveDataWrapper) = liveDataWrapper.setData(
        JokeViewModel.UiState.Loaded(getText(), getIconResId())
    )

    data class Base(private val setup: String, private val delivery: String) :
        JokeUiEntity(setup, delivery) {
        override fun getIconResId() = R.drawable.ic_unfavorite
    }

    class Favorite(setup: String, delivery: String) : JokeUiEntity(setup, delivery) {
        override fun getIconResId() = R.drawable.ic_favorite
    }

    data class Failed(private val text: String) : JokeUiEntity(text, "") {
        override fun getIconResId() = 0

        override fun getText() = text
    }
}