package com.isdenmois.readish.home.presentational

import android.content.Context
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.model.Status
import com.isdenmois.readish.home.repository.BookRepository
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

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        every { context.cacheDir } returns File("/tmp/readish-cache")
        repository = mockk()
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
        val viewModel = HomeViewModel(context, repository)

        // Act
        viewModel.loadBooks()

        // Assert
        assertEquals(Status.LOADING, viewModel.bookList.value.status)
        assertEquals(Status.LOADING, viewModel.fileList.value.status)
    }

    @Test
    fun loadBooks_publishesBooksAndLatestFiles() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val book = bookOf("Dune")
        val file = EBookFile(title = "Neuromancer", author = "Gibson", file = File("neuromancer.fb2"), cover = null)
        stubRepository(books = listOf(book), files = listOf(file))
        val viewModel = HomeViewModel(context, repository)

        // Act
        viewModel.loadBooks()
        advanceUntilIdle()

        // Assert
        assertEquals(Status.SUCCESS, viewModel.bookList.value.status)
        assertEquals(listOf(book), viewModel.bookList.value.data)
        assertEquals(Status.SUCCESS, viewModel.fileList.value.status)
        assertEquals(listOf(file), viewModel.fileList.value.data)
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
