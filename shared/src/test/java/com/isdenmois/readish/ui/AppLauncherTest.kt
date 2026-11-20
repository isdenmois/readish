package com.isdenmois.readish.ui

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AppLauncherTest {
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun launch_startsTheLauncherIntentOfThePackage() {
        // Arrange
        installLauncherApp("com.example.calculator")

        // Act
        AppLauncher(context).launch("com.example.calculator")

        // Assert
        val started = shadowOf(context).nextStartedActivity
        assertEquals("com.example.calculator", started?.component?.packageName)
        assertNull(ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun launch_packageWithoutLauncher_showsCantStartToast() {
        // Act
        AppLauncher(context).launch("com.example.missing")

        // Assert
        assertNull(shadowOf(context).nextStartedActivity)
        assertEquals(
            context.getString(R.string.cant_start_application),
            ShadowToast.getTextOfLatestToast(),
        )
    }

    private fun installLauncherApp(packageName: String) {
        val component = ComponentName(packageName, "$packageName.MainActivity")

        shadowOf(context.packageManager).apply {
            addOrUpdateActivity(
                ActivityInfo().apply {
                    name = component.className
                    this.packageName = packageName
                },
            )
            addIntentFilterForActivity(
                component,
                IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) },
            )
        }
    }
}
