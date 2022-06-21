package com.k_antonov.jokes.data.local

import com.k_antonov.jokes.data.Joke
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.Result

interface LocalDataSource {

    suspend fun getJoke(): Result<Joke, Unit>

    suspend fun addOrRemove(id: Int, joke: Joke): JokeUiEntity

    class Base(private val realmProvider: RealmProvider) : LocalDataSource {

        override suspend fun getJoke(): Result<Joke, Unit> {
            realmProvider.provide().use {
                val jokes = it.where(JokeRealm::class.java).findAll()
                if (jokes.isEmpty()) {
                    return Result.Error(Unit)
                } else {
                    jokes.random().let { jokeRealm ->
                        return Result.Success(
                            Joke(
                                error = jokeRealm.error,
                                category = jokeRealm.category,
                                type = jokeRealm.type,
                                setup = jokeRealm.setup,
                                delivery = jokeRealm.delivery,
                                flags = jokeRealm.flags!!.toJokeFlags(),
                                id = jokeRealm.id,
                                safe = jokeRealm.safe,
                                lang = jokeRealm.lang
                            )
                        )
                    }
                }
            }
        }

        override suspend fun addOrRemove(id: Int, joke: Joke): JokeUiEntity {
            realmProvider.provide().use {
                val jokeRealm = it.where(JokeRealm::class.java).equalTo("id", id).findFirst()
                return if (jokeRealm == null) {
                    it.executeTransaction { transaction ->
                        val newJoke = joke.toJokeRealm()
                        transaction.insert(newJoke)
                    }
                    joke.toFavoriteJoke()
                } else {
                    it.executeTransaction {
                        jokeRealm.deleteFromRealm()
                    }
                    joke.toBaseJoke()
                }
            }
        }
    }

}