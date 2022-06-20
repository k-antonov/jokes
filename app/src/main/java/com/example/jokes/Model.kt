package com.example.jokes

interface Model {

    fun getJoke()

    fun init(callback: JokeCallback)

    fun clear()

    fun changeJokeStatus(jokeCallback: JokeCallback)

    fun chooseDataSource(favorites: Boolean)

    class Base(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val resourceManager: ResourceManager
    ) : Model {

        private val noConnection by lazy { JokeError.NoConnection(resourceManager) }
        private val serviceUnavailable by lazy { JokeError.ServiceUnavailable(resourceManager) }
        private val noCachedJokes by lazy { JokeError.NoCachedJokes(resourceManager) }

        private var jokeCallback: JokeCallback? = null
        private var localJoke: Joke? = null

        private var getLocalJoke = false

        override fun getJoke() {
            if (getLocalJoke) {
                localDataSource.getJoke(object : JokeLocalCallback {
                    override fun provide(joke: Joke) {
                        localJoke = joke
                        jokeCallback?.provide(joke.toFavoriteJoke())
                    }

                    override fun fail() {
                        localJoke = null
                        jokeCallback?.provide(JokeUiEntity.Failed(noCachedJokes.getMessage()))
                    }
                })
            } else {
                remoteDataSource.getJoke(object : JokeRemoteCallback {
                    override fun provide(joke: Joke) {
                        localJoke = joke
                        jokeCallback?.provide(joke.toBaseJoke())
                    }

                    override fun fail(errorType: ErrorType) {
                        localJoke = null
                        val failure =
                            if (errorType == ErrorType.SERVICE_UNAVAILABLE) serviceUnavailable else noConnection
                        jokeCallback?.provide(JokeUiEntity.Failed(failure.getMessage()))
                    }
                })
            }
        }

        override fun init(callback: JokeCallback) {
            jokeCallback = callback
        }

        override fun changeJokeStatus(jokeCallback: JokeCallback) {
            localJoke?.changeStatus(localDataSource)?.let {
                jokeCallback.provide(it)
            }
        }

        override fun chooseDataSource(local: Boolean) {
            getLocalJoke = local
        }

        override fun clear() {
            jokeCallback = null
        }
    }

    class Test(resourceManager: ResourceManager) : Model {

        private var callback: JokeCallback? = null
        private var count = 0
        private val noConnection = JokeError.NoConnection(resourceManager)
        private val serviceUnavailable = JokeError.ServiceUnavailable(resourceManager)

        override fun getJoke() {
            Thread {
                Thread.sleep(1000)
                when (count) {
                    0 -> callback?.provide(JokeUiEntity.Base("Setup", "Punchline!"))
                    1 -> callback?.provide(JokeUiEntity.Favorite("Favorite joke", "<3"))
                    2 -> callback?.provide(JokeUiEntity.Failed(serviceUnavailable.getMessage()))
                }
                count++
                if (count == 3) count = 0
            }.start()
        }

        override fun init(callback: JokeCallback) {
            this.callback = callback
        }

        override fun changeJokeStatus(jokeCallback: JokeCallback) {
            TODO("Not yet implemented")
        }

        override fun chooseDataSource(favorites: Boolean) {
            TODO("Not yet implemented")
        }

        override fun clear() {
            callback = null
        }
    }
}

interface JokeCallback {

    fun provide(jokeUiEntity: JokeUiEntity)
}