package com.k_antonov.jokes.utils

import android.content.Context
import androidx.annotation.StringRes

interface ResourceManager {

    fun getString(@StringRes stringResId: Int): String

    class Base(private val context: Context) : ResourceManager {
        override fun getString(stringResId: Int) =
            context.getString(stringResId)
    }
}