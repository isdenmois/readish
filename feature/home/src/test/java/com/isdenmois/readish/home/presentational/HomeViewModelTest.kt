package com.isdenmois.readish.home.presentational

import android.content.Context
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.repository.BookRepository
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.ReaderLauncher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var context: Context
    private lateinit var repository: BookRepository
    private lateinit var readerLauncher: ReaderLauncher

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        every { context.cacheDir } returns File("/tmp/readish-cache")
        repository = mockk()
        readerLauncher = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadBooks_reportsLoadingBeforeResultsArrive() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubRepository(books = emptyList(), files = emptyList())
        val viewModel = HomeViewModel(context, repository, readerLauncher)

        // Act
        viewModel.loadBooks()

        // Assert
        assertEquals(LoadState.Loading, viewModel.uiState.value.books)
        assertEquals(LoadState.Loading, viewModel.uiState.value.latest)
    }

    @Test
    fun loadBooks_publishesBooksAndLatestFiles() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val book = bookOf("Dune")
        val file = EBookFile(title = "Neuromancer", author = "Gibson", file = File("neuromancer.fb2"), cover = null)
        stubRepository(books = listOf(book), files = listOf(file))
        val viewModel = HomeViewModel(context, repository, readerLauncher)

        // Act
        viewModel.loadBooks()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Success(listOf(book)), viewModel.uiState.value.books)
        assertEquals(LoadState.Success(listOf(file)), viewModel.uiState.value.latest)
    }

    @Test
    fun loadBooks_reportsRepositoryFailure() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getCurrentBooks(4) } throws IllegalStateException("provider unavailable")
        coEvery { repository.getLatestAddedBooks(6) } returns emptyList()
        val viewModel = HomeViewModel(context, repository, readerLauncher)

        // Act
        viewModel.loadBooks()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Error("provider unavailable"), viewModel.uiState.value.books)
        assertEquals(LoadState.Success(emptyList<EBookFile>()), viewModel.uiState.value.latest)
    }

    private fun stubRepository(books: List<Book>, files: List<EBookFile>) {
        coEvery { repository.getCurrentBooks(4) } returns books
        coEvery { repository.getLatestAddedBooks(6) } returns files
    }

    private fun bookOf(title: String) = Book(
        id = 1,
        title = title,
        author = "Author",
        size = 2048,
        position = 1024,
        readTime = 0,
        file = File("$title.fb2"),
        cover = null,
    )
}
