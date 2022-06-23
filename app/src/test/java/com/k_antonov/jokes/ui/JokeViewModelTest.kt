package com.k_antonov.jokes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.k_antonov.jokes.data.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class JokeViewModelTest {

    private lateinit var model: TestModel
    private lateinit var liveDataWrapper: TestLiveDataWrapper
    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var viewModel: JokeViewModel

    @Before
    fun setup() {
        model = TestModel()
        liveDataWrapper = TestLiveDataWrapper()
        Dispatchers.setMain(dispatcher)
        viewModel = JokeViewModel(model, liveDataWrapper, dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get successful joke from remote`() = runBlocking {
        model.isResultSuccess = true
        viewModel.chooseFavorites(favorites = false)
        viewModel.getJoke()

        val expectedUiText = "remote setup\nremote delivery"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId
        assertEquals(expectedUiText, actualUiText)
        assertNotEquals(0, actualImageResId)
    }

    @Test
    fun `get failed joke from remote`() = runBlocking {
        model.isResultSuccess = false
        viewModel.chooseFavorites(false)
        viewModel.getJoke()

        val expectedUiText = "remote failed\n"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId
        assertEquals(expectedUiText, actualUiText)
        assertEquals(0, actualImageResId)
    }

    @Test
    fun `get successful joke from local`() = runBlocking {
        model.isResultSuccess = true
        viewModel.chooseFavorites(favorites = true)
        viewModel.getJoke()

        val expectedUiText = "local setup\nlocal delivery"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId
        assertEquals(expectedUiText, actualUiText)
        assertNotEquals(0, actualImageResId)
    }

    @Test
    fun `get failed joke from local`() = runBlocking {
        model.isResultSuccess = false
        viewModel.chooseFavorites(favorites = true)
        viewModel.getJoke()

        val expectedUiText = "local failed\n"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId
        assertEquals(expectedUiText, actualUiText)
        assertEquals(0, actualImageResId)
    }

    @Test
    fun `change joke status from base to favorite`() = runBlocking {
        `get successful joke from local`()
        viewModel.changeJokeStatus()

        val expectedUiText = "local favorite setup\nlocal favorite delivery"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId

        assertEquals(expectedUiText, actualUiText)
        assertNotEquals(0, actualImageResId)
    }

    @Test
    fun `change joke status from favorite to base`() = runBlocking {
        `change joke status from base to favorite`()
        viewModel.changeJokeStatus()

        val expectedUiText = "local setup\nlocal delivery"
        val actualUiText = liveDataWrapper.uiText
        val actualImageResId = liveDataWrapper.imageResId

        assertEquals(expectedUiText, actualUiText)
        assertNotEquals(0, actualImageResId)
    }

    @Test
    fun observe() = runBlocking {
        val owner = LifecycleOwner { TODO("Not yet implemented") }
        val observer = Observer<Pair<String, Int>> {}
        viewModel.observe(owner, observer)

        assertEquals(true, liveDataWrapper.observed)
    }

    private inner class TestModel : Model {

        private val localJokeUiEntityBase = JokeUiEntity.Base("local setup", "local delivery")
        private val localJokeUiEntityFailed = JokeUiEntity.Failed("local failed")
        private val remoteJokeUiEntityBase = JokeUiEntity.Base("remote setup", "remote delivery")
        private val remoteJokeUiEntityFailed = JokeUiEntity.Failed("remote failed")

        private val localJokeUiEntityFavorite = JokeUiEntity.Favorite("local favorite setup", "local favorite delivery")

        var isResultSuccess = false
        private var getLocal = false
        private var localJokeUiEntity: JokeUiEntity? = null

        override suspend fun getJoke(): JokeUiEntity {
            return if (isResultSuccess) {
                if (getLocal) {
                    localJokeUiEntityBase.also {
                        localJokeUiEntity = it
                    }
                } else {
                    remoteJokeUiEntityBase.also {
                        localJokeUiEntity = it
                    }
                }
            } else {
                localJokeUiEntity = null
                if (getLocal) {
                    localJokeUiEntityFailed
                } else {
                    remoteJokeUiEntityFailed
                }
            }
        }

        override suspend fun changeJokeStatus(): JokeUiEntity {
            return if (localJokeUiEntity is JokeUiEntity.Base) {
                localJokeUiEntityFavorite.also {
                    localJokeUiEntity = it
                }
            } else {
                localJokeUiEntityBase.also {
                    localJokeUiEntity = it
                }
            }
        }

        override fun chooseDataSource(local: Boolean) {
            getLocal = local
        }
    }

    private inner class TestLiveDataWrapper : LiveDataWrapper {

        var uiText = ""
        var imageResId = -1 // 0 for Failed, not 0 for Success
        var observed = false

        override fun setData(data: Pair<String, Int>) {
            uiText = data.first
            imageResId = data.second
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<Pair<String, Int>>) {
            observed = true
        }
    }
}