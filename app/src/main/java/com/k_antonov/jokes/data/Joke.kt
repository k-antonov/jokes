package com.k_antonov.jokes.data

import com.k_antonov.jokes.data.local.JokeRealm
import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.local.RealmFlags
import com.k_antonov.jokes.ui.JokeUiEntity

class Joke(
    private val error: Boolean,
    private val category: String,
    private val type: String,
    private val setup: String,
    private val delivery: String,
    private val flags: Flags,
    private val id: Int,
    private val safe: Boolean,
    private val lang: String
) {
    class Flags(
        private val nsfw: Boolean,
        private val religious: Boolean,
        private val political: Boolean,
        private val racist: Boolean,
        private val sexist: Boolean,
        private val explicit: Boolean
    ) {
        fun toRealm(): RealmFlags = RealmFlags(
            nsfw = nsfw,
            religiuos = religious,
            political = political,
            racist = racist,
            sexist = sexist,
            explicit = explicit
        )
    }

    fun toJokeUiBase() = JokeUiEntity.Base(setup, delivery)

    fun toJokeUiFavorite() = JokeUiEntity.Favorite(setup, delivery)

    fun toJokeRealm(): JokeRealm {
        return JokeRealm().also {
            it.error = error
            it.category = category
            it.type = type
            it.setup = setup
            it.delivery = delivery
            it.flags = flags.toRealm()
            it.id = id
            it.safe = safe
            it.lang = lang
        }
    }

    suspend fun changeStatus(localDataSource: LocalDataSource) = localDataSource.addOrRemove(id, this)
}