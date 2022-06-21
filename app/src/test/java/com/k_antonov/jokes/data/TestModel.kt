package com.k_antonov.jokes.data

import com.k_antonov.jokes.data.local.LocalDataSource
import com.k_antonov.jokes.data.remote.ErrorType
import com.k_antonov.jokes.data.remote.JokeRemoteEntity
import com.k_antonov.jokes.data.remote.RemoteDataSource
import com.k_antonov.jokes.ui.JokeUiEntity
import com.k_antonov.jokes.utils.ResourceManager
import com.k_antonov.jokes.utils.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TestModel {

    private lateinit var remoteDataSource: TestRemoteDataSource
    private lateinit var localDataSource: TestLocalDataSource
    private lateinit var resourceManager: TestResourceManager
    private var localJoke: Joke? = null
    private lateinit var model: Model

    @Before
    fun setup() {
        remoteDataSource = TestRemoteDataSource()
        localDataSource = TestLocalDataSource()
        resourceManager = TestResourceManager("A message from ResourceManager")
        model = Model.Base(remoteDataSource, localDataSource, resourceManager)
    }

    @Test
    fun `model changes data source successfully`() = runBlocking {
        remoteDataSource.isResultSuccess = true
        model.chooseDataSource(local = false)
        val jokeUiEntity = model.getJoke()
        assertEquals(true, jokeUiEntity is JokeUiEntity.Base)

        model.changeJokeStatus()
        assertEquals(true, localDataSource.checkContainsId(0))
    }

    @Test
    fun `model saves multiple jokes`() = runBlocking {
        remoteDataSource.isResultSuccess = true
        model.chooseDataSource(local = false)
        for (i in 0..10) {
            localJoke = null
            model.getJoke()
            model.changeJokeStatus()
            assertNotNull(localJoke)
            assertEquals(true, localDataSource.checkContainsId(i))

            model.changeJokeStatus()
            assertEquals(false, localDataSource.checkContainsId(i))
        }
    }

    @Test
    fun `if remote data source returns error model returns failure`() = runBlocking {
        model.chooseDataSource(local = false)
        remoteDataSource.isResultSuccess = false
        val jokeUiEntity = model.getJoke()
        assertEquals(true, jokeUiEntity is JokeUiEntity.Failed)
    }

    @Test
    fun `if local data source returns error model returns failure`() = runBlocking {
        model.chooseDataSource(local = true)
        localDataSource.isResultSuccess = false
        val jokeUiEntity = model.getJoke()
        assertEquals(true, jokeUiEntity is JokeUiEntity.Failed)
    }

    private inner class TestRemoteDataSource : RemoteDataSource {

        var isResultSuccess = true
        private var count = 0

        override suspend fun getJoke(): Result<JokeRemoteEntity, ErrorType> {
            return if (isResultSuccess) {
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
                localJoke = jokeRemoteEntity.toJoke()
                Result.Success(jokeRemoteEntity)
            } else {
                localJoke = null
                Result.Error(ErrorType.NO_CONNECTION)
            }
        }
    }

    private inner class TestLocalDataSource : LocalDataSource {

        var isResultSuccess = true
        private val map = HashMap<Int, Joke>()
        private var nextJokeId = -1

        override suspend fun getJoke(): Result<Joke, Unit> {
            return if (isResultSuccess) {
                val joke = map[nextJokeId]!!
                localJoke = joke
                Result.Success(joke)
            } else {
                localJoke = null
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
                joke.toJokeUiFavorite()
            }
        }

        fun checkContainsId(id: Int) = map.containsKey(id)
    }

    private class TestResourceManager(private val testMessage: String) : ResourceManager {
        override fun getString(stringResId: Int) = testMessage
    }
}