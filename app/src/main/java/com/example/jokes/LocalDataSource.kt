package com.example.jokes

import io.realm.Realm

interface LocalDataSource {

    fun getJoke(jokeLocalCallback: JokeLocalCallback)

    fun addOrRemove(id: Int, joke: Joke): JokeUiEntity

    class Base(private val realm: Realm) : LocalDataSource {

        override fun getJoke(jokeLocalCallback: JokeLocalCallback) {
            realm.let {
                val jokes = it.where(JokeRealm::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeLocalCallback.fail()
                } else {
                    jokes.random().let { jokeRealm ->
                        jokeLocalCallback.provide(
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

        override fun addOrRemove(id: Int, joke: Joke): JokeUiEntity {
            realm.let {
                val jokeRealm = it.where(JokeRealm::class.java).equalTo("id", id).findFirst()
                return if (jokeRealm == null) {
                    val newJoke = joke.toJokeRealm()
                    it.executeTransactionAsync { transaction ->
                        transaction.insert(newJoke)
                    }
                    joke.toFavoriteJoke()
                } else {
                    it.executeTransactionAsync {
                        jokeRealm.deleteFromRealm()
                    }
                    joke.toBaseJoke()
                }
            }
        }
    }

    class Test : LocalDataSource {

        private val list = mutableListOf<Pair<Int, Joke>>()

        override fun getJoke(jokeLocalCallback: JokeLocalCallback) {
            if (list.isEmpty()) {
                jokeLocalCallback.fail()
            } else {
                jokeLocalCallback.provide(list.random().second)
            }
        }

        override fun addOrRemove(id: Int, joke: Joke): JokeUiEntity {
           val found = list.find { it.first == id }
            return if (found != null) {
                val jokeUiEntity = found.second.toBaseJoke()
                list.remove(found)
                jokeUiEntity
            } else {
                list.add(Pair(id, joke))
                joke.toFavoriteJoke()
            }
        }
    }
}