package com.example.jokes

import io.realm.Realm

interface LocalDataSource {

    fun getJoke(jokeLocalCallback: JokeLocalCallback)

    fun addOrRemove(id: Int, jokeRemoteEntity: JokeRemoteEntity): Joke


    class Base(private val realm: Realm) : LocalDataSource {

        override fun getJoke(jokeLocalCallback: JokeLocalCallback) {
            realm.let {
                val jokes = it.where(JokeRealm::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeLocalCallback.fail()
                } else {
                    jokes.random().let { jokeRealm ->
                        jokeLocalCallback.provide(
                            JokeRemoteEntity(
                                error = jokeRealm.error,
                                category = jokeRealm.category,
                                type = jokeRealm.type,
                                setup = jokeRealm.setup,
                                delivery = jokeRealm.delivery,
                                flags = jokeRealm.flags!!.toRemote(),
                                id = jokeRealm.id,
                                safe = jokeRealm.safe,
                                lang = jokeRealm.lang
                            )
                        )
                    }
                }
            }
        }

        override fun addOrRemove(id: Int, jokeRemoteEntity: JokeRemoteEntity): Joke {
            realm.let {
                val jokeRealm = it.where(JokeRealm::class.java).equalTo("id", id).findFirst()
                return if (jokeRealm == null) {
                    val newJoke = jokeRemoteEntity.toJokeRealm()
                    it.executeTransactionAsync { transaction ->
                        transaction.insert(newJoke)
                    }
                    jokeRemoteEntity.toFavoriteJoke()
                } else {
                    it.executeTransactionAsync {
                        jokeRealm.deleteFromRealm()
                    }
                    jokeRemoteEntity.toBaseJoke()
                }
            }
        }
    }

    class Test : LocalDataSource {

        private val list = mutableListOf<Pair<Int, JokeRemoteEntity>>()

        override fun getJoke(jokeLocalCallback: JokeLocalCallback) {
            if (list.isEmpty()) {
                jokeLocalCallback.fail()
            } else {
                jokeLocalCallback.provide(list.random().second)
            }
        }

        override fun addOrRemove(id: Int, jokeRemoteEntity: JokeRemoteEntity): Joke {
           val found = list.find { it.first == id }
            return if (found != null) {
                val joke = found.second.toBaseJoke()
                list.remove(found)
                joke
            } else {
                list.add(Pair(id, jokeRemoteEntity))
                jokeRemoteEntity.toFavoriteJoke()
            }
        }
    }
}