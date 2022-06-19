package com.example.jokes

interface Model {

    fun getJoke()

    fun init(callback: ResultCallback)

    fun clear()

    class Base(
        private val service: JokeService,
        private val resourceManager: ResourceManager
    ) : Model {

        private var callback: ResultCallback? = null
        private val noConnection by lazy { JokeError.NoConnection(resourceManager) }
        private val serviceUnavailable by lazy { JokeError.ServiceUnavailable(resourceManager) }

        override fun getJoke() {
            service.getJoke(object : ServiceCallback {
                override fun returnSuccess(data: String) {
                    callback?.provideSuccess(Joke(data, ""))
                }

                override fun returnError(errorType: ErrorType) {
                    when (errorType) {
                        ErrorType.NO_CONNECTION -> callback?.provideError(noConnection)
                        ErrorType.OTHER -> callback?.provideError(serviceUnavailable)
                    }
                }
            })
        }

        override fun init(callback: ResultCallback) {
            this.callback = callback
        }

        override fun clear() {
            callback = null
        }
    }


    class Test(resourceManager: ResourceManager) : Model {

        private var callback: ResultCallback? = null
        private var count = 0
        private val noConnection = JokeError.NoConnection(resourceManager)
        private val serviceUnavailable = JokeError.ServiceUnavailable(resourceManager)

        override fun getJoke() {
            Thread {
                Thread.sleep(1000)
                when (count) {
                    0 -> callback?.provideSuccess(Joke("Setup", "Punchline!"))
                    1 -> callback?.provideError(noConnection)
                    2 -> callback?.provideError(serviceUnavailable)
                }
                count++
                if (count == 3) count = 0
            }.start()
        }

        override fun init(callback: ResultCallback) {
            this.callback = callback
        }

        override fun clear() {
            callback = null
        }
    }
}

interface ResultCallback {

    fun provideSuccess(joke: Joke)

    fun provideError(error: JokeError)
}