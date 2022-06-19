package com.example.jokes

interface LocalDataSource {

    fun addOrRemove(id: Int, jokeRemoteEntity: JokeRemoteEntity): Joke

    fun getJoke(jokeLocalCallback: JokeLocalCallback)

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