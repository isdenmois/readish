package com.isdenmois.readish.apps.repository

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AppRepositoryTest {
    private val context = RuntimeEnvironment.getApplication()
    private val repository = AppRepository(context)

    @Test
    fun getLauncherApps_listsOnlyLauncherActivitiesSortedByLabel() = runTest {
        // Arrange
        installApp("com.example.zebra", label = "Zebra")
        installApp("com.example.alpha", label = "alpha")
        installApp("com.example.service", label = "Middle", launcher = false)

        // Act
        val apps = repository.getLauncherApps()

        // Assert
        assertEquals(listOf("alpha", "Zebra"), apps.map { it.label })
    }

    @Test
    fun getLauncherApps_excludesReadishItself() = runTest {
        // Arrange
        installApp(context.packageName, label = "Readish")
        installApp("com.example.notes", label = "Notes")

        // Act
        val apps = repository.getLauncherApps()

        // Assert
        assertEquals(listOf("Notes"), apps.map { it.label })
    }

    @Test
    fun getLauncherApps_keepsOneTilePerPackage() = runTest {
        // Arrange
        installLauncherActivity("com.example.dual", "com.example.dual.MainActivity")
        installLauncherActivity("com.example.dual", "com.example.dual.OtherActivity", packageLabel = "Dual")

        // Act
        val apps = repository.getLauncherApps()

        // Assert
        assertEquals(1, apps.count { it.packageName == "com.example.dual" })
    }

    @Test
    fun getLauncherApps_loadsAnIconForEachApp() = runTest {
        // Arrange
        installApp("com.example.icon", label = "Icon")

        // Act
        val apps = repository.getLauncherApps()

        // Assert
        val icon = apps.single().icon
        assertNotNull(icon)
        assertTrue(icon!!.width > 0 && icon.height > 0)
    }

    private fun installApp(packageName: String, label: String, launcher: Boolean = true) {
        installLauncherActivity(packageName, "$packageName.MainActivity", packageLabel = label, launcher = launcher)
    }

    private fun installLauncherActivity(
        packageName: String,
        activityName: String,
        packageLabel: String = packageName.substringAfterLast('.'),
        launcher: Boolean = true,
    ) {
        val pm = shadowOf(context.packageManager)

        pm.addPackage(
            PackageInfo().apply {
                this.packageName = packageName
                applicationInfo = ApplicationInfo().apply {
                    this.packageName = packageName
                    nonLocalizedLabel = packageLabel
                    icon = android.R.drawable.ic_menu_compass
                }
            },
        )
        pm.addOrUpdateActivity(
            ActivityInfo().apply {
                name = activityName
                this.packageName = packageName
            },
        )

        if (launcher) {
            pm.addIntentFilterForActivity(
                ComponentName(packageName, activityName),
                IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) },
            )
        }
    }
}
