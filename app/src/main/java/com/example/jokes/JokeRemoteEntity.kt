package com.example.jokes

import com.google.gson.annotations.SerializedName

data class JokeRemoteEntity(
    @SerializedName("error")
    private val error: Boolean,
    @SerializedName("category")
    private val category: String,
    @SerializedName("type")
    private val type: String,
    @SerializedName("setup")
    private val setup: String,
    @SerializedName("delivery")
    private val delivery: String,
    @SerializedName("flags")
    private val flags: Flags,
    @SerializedName("id")
    private val id: Int,
    @SerializedName("safe")
    private val safe: Boolean,
    @SerializedName("lang")
    private val lang: String
) {
    data class Flags(
        @SerializedName("nsfw")
        private val nsfw: Boolean,
        @SerializedName("religious")
        private val religious: Boolean,
        @SerializedName("political")
        private val political: Boolean,
        @SerializedName("racist")
        private val racist: Boolean,
        @SerializedName("sexist")
        private val sexist: Boolean,
        @SerializedName("explicit")
        private val explicit: Boolean
    ) {
        fun toRealm() = RealmFlags(
            nsfw = nsfw,
            religiuos = religious,
            political = political,
            racist = racist,
            sexist = sexist,
            explicit = explicit
        )
    }

    fun toBaseJoke() = Joke.Base(setup, delivery)

    fun toFavoriteJoke() = Joke.Favorite(setup, delivery)

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

    fun changeStatus(localDataSource: LocalDataSource) = localDataSource.addOrRemove(id, this)
}