package com.isdenmois.readish.library.presentational

import android.content.Context
import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.library.model.SortField
import com.isdenmois.readish.library.repository.LibraryRepository
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.ReaderLauncher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {
    private lateinit var context: Context
    private lateinit var repository: LibraryRepository
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
        coEvery { repository.getBooks() } returns listOf(book("dune.fb2"))
        val viewModel = viewModel()

        // Act
        viewModel.loadBooks()

        // Assert
        assertEquals(LoadState.Loading, viewModel.uiState.value.books)
    }

    @Test
    fun loadBooks_publishesBooks() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val books = listOf(book("dune.fb2"), book("solaris.fb2"))
        coEvery { repository.getBooks() } returns books
        val viewModel = viewModel()

        // Act
        viewModel.loadBooks()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Success(books), viewModel.uiState.value.books)
    }

    @Test
    fun loadBooks_reportsRepositoryFailure() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } throws IllegalStateException("folder unavailable")
        val viewModel = viewModel()

        // Act
        viewModel.loadBooks()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Error("folder unavailable"), viewModel.uiState.value.books)
    }

    @Test
    fun loadBooks_resetsPageToFirst() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } returns (1..13).map { book("book-$it.fb2") }
        val viewModel = viewModel()
        viewModel.loadBooks()
        advanceUntilIdle()
        viewModel.nextPage()

        // Act
        viewModel.loadBooks()

        // Assert
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun setSort_sameFieldFlipsDirection() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = viewModel()

        // Act
        viewModel.setSort(SortField.TIME)

        // Assert
        assertTrue(viewModel.uiState.value.ascending)
    }

    @Test
    fun setSort_newTimeSortStartsNewestFirst() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = viewModel()
        viewModel.setSort(SortField.TITLE)

        // Act
        viewModel.setSort(SortField.TIME)

        // Assert
        assertEquals(SortField.TIME, viewModel.uiState.value.sort)
        assertEquals(false, viewModel.uiState.value.ascending)
    }

    @Test
    fun setSort_newTitleAndFilenameSortsStartAlphabetically() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = viewModel()

        // Act
        viewModel.setSort(SortField.TITLE)
        val afterTitle = viewModel.uiState.value

        viewModel.setSort(SortField.FILENAME)
        val afterFilename = viewModel.uiState.value

        // Assert
        assertEquals(SortField.TITLE, afterTitle.sort)
        assertTrue(afterTitle.ascending)
        assertEquals(SortField.FILENAME, afterFilename.sort)
        assertTrue(afterFilename.ascending)
    }

    @Test
    fun setSort_resetsPageToFirst() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } returns (1..13).map { book("book-$it.fb2") }
        val viewModel = viewModel()
        viewModel.loadBooks()
        advanceUntilIdle()
        viewModel.nextPage()

        // Act
        viewModel.setSort(SortField.TITLE)

        // Assert
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun setFilter_resetsPageAndKeepsText() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } returns (1..13).map { book("book-$it.fb2") }
        val viewModel = viewModel()
        viewModel.loadBooks()
        advanceUntilIdle()
        viewModel.nextPage()

        // Act
        viewModel.setFilter("book-1")

        // Assert
        assertEquals("book-1", viewModel.uiState.value.filter)
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun nextPage_movesForwardWithinBounds() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } returns (1..13).map { book("book-$it.fb2") }
        val viewModel = viewModel()
        viewModel.loadBooks()
        advanceUntilIdle()

        // Act
        viewModel.nextPage()
        viewModel.nextPage()

        // Assert
        assertEquals(1, viewModel.uiState.value.page)
    }

    @Test
    fun previousPage_doesNotGoBelowFirst() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getBooks() } returns (1..13).map { book("book-$it.fb2") }
        val viewModel = viewModel()
        viewModel.loadBooks()
        advanceUntilIdle()

        // Act
        viewModel.previousPage()

        // Assert
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun openBook_launchesTheFile() = runTest {
        // Arrange
        val viewModel = viewModel()
        val book = book("dune.fb2")

        // Act
        viewModel.openBook(book)

        // Assert
        verify { readerLauncher.openBook(book.file) }
    }

    private fun viewModel() = LibraryViewModel(context, repository, readerLauncher)

    private fun book(fileName: String) = LibraryBook(
        file = File(fileName),
        title = fileName.substringBeforeLast('.'),
        author = "Author",
        cover = null,
        modifiedAt = 0,
    )
}
