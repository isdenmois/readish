package com.isdenmois.readish.apps.presentational

import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.apps.repository.AppRepository
import com.isdenmois.readish.ui.AppLauncher
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppsViewModelTest {
    private lateinit var repository: AppRepository
    private lateinit var appLauncher: AppLauncher
    private lateinit var readerLauncher: ReaderLauncher

    @Before
    fun setUp() {
        repository = mockk()
        appLauncher = mockk(relaxed = true)
        readerLauncher = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadApps_reportsLoadingBeforeResultsArrive() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } returns apps(25)
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)

        // Act
        viewModel.loadApps()

        // Assert
        assertEquals(LoadState.Loading, viewModel.uiState.value.apps)
    }

    @Test
    fun loadApps_publishesTheLauncherApps() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } returns apps(25)
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)

        // Act
        viewModel.loadApps()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Success(apps(25)), viewModel.uiState.value.apps)
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun loadApps_reportsRepositoryFailure() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } throws IllegalStateException("package manager unavailable")
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)

        // Act
        viewModel.loadApps()
        advanceUntilIdle()

        // Assert
        assertEquals(LoadState.Error("package manager unavailable"), viewModel.uiState.value.apps)
    }

    @Test
    fun nextPage_andPreviousPage_stopAtTheEdges() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } returns apps(25)
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)
        viewModel.loadApps()
        advanceUntilIdle()

        // Act and assert
        viewModel.nextPage()
        assertEquals(1, viewModel.uiState.value.page)

        viewModel.nextPage()
        assertEquals(1, viewModel.uiState.value.page)

        viewModel.previousPage()
        assertEquals(0, viewModel.uiState.value.page)

        viewModel.previousPage()
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun loadApps_keepsThePageAndClampsItWhenTheListShrinks() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } returns apps(25)
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)
        viewModel.loadApps()
        advanceUntilIdle()
        viewModel.nextPage()

        // Act
        coEvery { repository.getLauncherApps() } returns apps(5)
        viewModel.loadApps()
        advanceUntilIdle()

        // Assert
        assertEquals(0, viewModel.uiState.value.page)
    }

    @Test
    fun loadApps_keepsThePageWhenTheListStillHasIt() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { repository.getLauncherApps() } returns apps(25)
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)
        viewModel.loadApps()
        advanceUntilIdle()
        viewModel.nextPage()

        // Act
        viewModel.loadApps()
        advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.uiState.value.page)
    }

    @Test
    fun openApp_launchesThePackagesLauncherIntent() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        every { appLauncher.launch(any()) } returns Unit
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)
        val app = apps(1).single()

        // Act
        viewModel.openApp(app)

        // Assert
        verify { appLauncher.launch(app.packageName) }
    }

    @Test
    fun openOnyxHome_opensTheOnyxHomeActivity() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        every { readerLauncher.openOnyxHome() } returns Unit
        val viewModel = AppsViewModel(repository, appLauncher, readerLauncher)

        // Act
        viewModel.openOnyxHome()

        // Assert
        verify { readerLauncher.openOnyxHome() }
    }

    private fun apps(count: Int) = (1..count).map {
        AppEntry(packageName = "com.example.app$it", label = "App $it", icon = null)
    }
}
