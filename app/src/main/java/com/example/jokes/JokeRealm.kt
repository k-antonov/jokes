package com.example.jokes

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

open class JokeRealm(
    var error: Boolean = false,
    var category: String = "",
    var type: String = "",
    var setup: String = "",
    var delivery: String = "",
    var flags: RealmFlags? = null,
    @PrimaryKey var id: Int = -1,
    var safe: Boolean = false,
    var lang: String = ""
) : RealmObject()

@RealmClass(embedded = true)
open class RealmFlags(
    var nsfw: Boolean? = null,
    var religiuos: Boolean? = null,
    var political: Boolean? = null,
    var racist: Boolean? = null,
    var sexist: Boolean? = null,
    var explicit: Boolean? = null
) : RealmObject() {

    fun toJokeFlags() = Joke.Flags(
        nsfw = nsfw!!,
        religious = religiuos!!,
        political = political!!,
        racist = racist!!,
        sexist = sexist!!,
        explicit = explicit!!
    )
}