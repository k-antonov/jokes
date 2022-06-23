package com.k_antonov.jokes.data

import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.local.LocalJoke
import com.k_antonov.jokes.data.remote.ErrorType
import com.k_antonov.jokes.data.remote.JokeRemoteEntity
import com.k_antonov.jokes.data.remote.RemoteDataSource
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TestModel {

    private lateinit var localDataSource: TestLocalDataSource
    private lateinit var remoteDataSource: TestRemoteDataSource
    private lateinit var localResultHandler: TestLocalResultHandler
    private lateinit var remoteResultHandler: TestRemoteResultHandler
    private lateinit var localJoke: LocalJoke

    private lateinit var model: Model

    @Before
    fun setup() {
        localDataSource = TestLocalDataSource()
        remoteDataSource = TestRemoteDataSource()
        localResultHandler = TestLocalResultHandler()
        remoteResultHandler = TestRemoteResultHandler()
        localJoke = LocalJoke.Base()

        model = Model.Base(
            localDataSource,
            localResultHandler,
            remoteResultHandler,
            localJoke
        )
    }

    @Test
    fun `get successful joke from remote`() = runBlocking {
        remoteDataSource.status = Status.SUCCESS
        model.chooseDataSource(local = false)

        val actualJokeUiEntity = model.getJoke()
        val expectedJokeUiEntity = JokeUiEntity.Base("test setup", "test delivery")
        assertEquals(expectedJokeUiEntity, actualJokeUiEntity)
    }

    @Test
    fun `get service unavailable error`() = runBlocking {
        remoteDataSource.status = Status.SERVICE_UNAVAILABLE
        model.chooseDataSource(local = false)

        val actualJokeUiEntity = model.getJoke()
        val expectedJokeUiEntity = JokeUiEntity.Failed("Test service unavailable")
        assertEquals(expectedJokeUiEntity, actualJokeUiEntity)
    }

    @Test
    fun `get no connection error`() = runBlocking {
        remoteDataSource.status = Status.NO_CONNECTION
        model.chooseDataSource(local = false)

        val actualJokeUiEntity = model.getJoke()
        val expectedJokeUiEntity = JokeUiEntity.Failed("Test no connection")
        assertEquals(expectedJokeUiEntity, actualJokeUiEntity)
    }

    @Test
    fun `change joke status from base to favorite`() = runBlocking {
        remoteDataSource.status = Status.SUCCESS
        model.chooseDataSource(local = false)

        model.getJoke()
        val expectedJokeUiEntity = model.changeJokeStatus()
        assertEquals(true, expectedJokeUiEntity is JokeUiEntity.Favorite)
        assertEquals(true, localDataSource.checkContainsId(0))
    }

    @Test
    fun `change joke status from favorite to base`() = runBlocking {
        `change joke status from base to favorite`()
        val expectedJokeUiEntity = model.changeJokeStatus()
        assertEquals(true, expectedJokeUiEntity is JokeUiEntity.Base)
        assertEquals(false, localDataSource.checkContainsId(0))
    }

    @Test
    fun `get successful joke from local`() = runBlocking {
        remoteDataSource.status = Status.SUCCESS
        model.chooseDataSource(local = false)
        model.getJoke()
        model.changeJokeStatus()

        localDataSource.isResultSuccess = true
        model.chooseDataSource(local = true)
        assertEquals(true, localDataSource.checkContainsId(0))

        localJoke.clear()
        val actualJokeUiEntity = model.getJoke()
        assertEquals(true, actualJokeUiEntity is JokeUiEntity.Favorite)
    }

    @Test
    fun `get failed joke from local`() = runBlocking {
        model.chooseDataSource(local = true)
        localDataSource.isResultSuccess = false
        val jokeUiEntity = model.getJoke()
        assertEquals(true, jokeUiEntity is JokeUiEntity.Failed)
    }

    private inner class TestRemoteDataSource : RemoteDataSource {
        var status = Status.SUCCESS
        private var count = 0

        override suspend fun getJoke(): Result<JokeRemoteEntity, ErrorType> = when (status) {
            Status.SUCCESS -> {
                val jokeRemoteEntity = JokeRemoteEntity(
                    error = false,
                    category = "test category",
                    type = "test type",
                    setup = "test setup",
                    delivery = "test delivery",
                    flags = JokeRemoteEntity.Flags(
                        nsfw = false,
                        religious = false,
                        political = false,
                        racist = false,
                        sexist = false,
                        explicit = false
                    ),
                    id = count++,
                    safe = false,
                    lang = "test lang"
                )
                localJoke.save(jokeRemoteEntity.toJoke())
                Result.Success(jokeRemoteEntity)
            }
            Status.NO_CONNECTION -> {
                localJoke.clear()
                Result.Error(ErrorType.NO_CONNECTION)
            }
            Status.SERVICE_UNAVAILABLE -> {
                localJoke.clear()
                Result.Error(ErrorType.SERVICE_UNAVAILABLE)
            }
        }
    }

    private enum class Status {
        SUCCESS,
        NO_CONNECTION,
        SERVICE_UNAVAILABLE
    }

    private inner class TestLocalDataSource : LocalDataSource {

        var isResultSuccess = true
        private val map = HashMap<Int, Joke>()
        private var nextJokeId = -1

        override suspend fun getJoke(): Result<Joke, Unit> {
            return if (isResultSuccess) {
                val joke = map[nextJokeId]!!
                localJoke.save(joke)
                Result.Success(joke)
            } else {
                localJoke.clear()
                Result.Error(Unit)
            }
        }

        override suspend fun addOrRemove(id: Int, joke: Joke): JokeUiEntity {
            return if (map.containsKey(id)) {
                val jokeUiEntity = map[id]!!.toJokeUiBase()
                map.remove(id)
                jokeUiEntity
            } else {
                map[id] = joke
                nextJokeId = id
                joke.toJokeUiFavorite()
            }
        }

        fun checkContainsId(id: Int) = map.containsKey(id)
    }

    private inner class TestRemoteResultHandler :
        ResultHandler<JokeRemoteEntity, ErrorType>(remoteDataSource) {

        override fun handleResult(result: Result<JokeRemoteEntity, ErrorType>): JokeUiEntity =
            when (result) {
                is Result.Success<JokeRemoteEntity> -> {
                    result.data.toJoke().let {
                        localJoke.save(it)
                        it.toJokeUiBase()
                    }
                }
                is Result.Error<ErrorType> -> {
                    localJoke.clear()
                    if (result.exception == ErrorType.SERVICE_UNAVAILABLE)
                        JokeUiEntity.Failed("Test service unavailable")
                    else
                        JokeUiEntity.Failed("Test no connection")
                }
            }
    }


    private inner class TestLocalResultHandler : ResultHandler<Joke, Unit>(localDataSource) {

        override fun handleResult(result: Result<Joke, Unit>): JokeUiEntity =
            when (result) {
                is Result.Success<Joke> -> result.data.let {
                    localJoke.save(it)
                    it.toJokeUiFavorite()
                }
                is Result.Error -> {
                    localJoke.clear()
                    JokeUiEntity.Failed("Test no cached jokes")
                }
            }
    }
}