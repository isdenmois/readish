package com.isdenmois.readish.apps.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import com.isdenmois.readish.apps.model.AppEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class AppRepository
@Inject
constructor(@ApplicationContext private val context: Context) {
    suspend fun getLauncherApps(): List<AppEntry> = withContext(Dispatchers.Default) {
        val iconSize = (ICON_DP * context.resources.displayMetrics.density).roundToInt()

        launcherPackages()
            .filter { it != context.packageName }
            .mapNotNull { entryOf(it, iconSize) }
            .sortedWith(
                compareBy<AppEntry, String>(String.CASE_INSENSITIVE_ORDER) {
                    it.label
                }.thenBy { it.packageName },
            )
    }

    private fun launcherPackages(): List<String> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(intent, 0)
            }

        return activities.mapNotNull { it.activityInfo?.packageName }.distinct()
    }

    private fun entryOf(packageName: String, iconSize: Int): AppEntry? = try {
        val info = applicationInfo(packageName)
        val label = context.packageManager.getApplicationLabel(info).toString().ifBlank { packageName }
        val icon = context.packageManager.getApplicationIcon(info).toBitmap(iconSize, iconSize)

        AppEntry(packageName = packageName, label = label, icon = icon)
    } catch (e: Exception) {
        null
    }

    private fun applicationInfo(packageName: String): ApplicationInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getApplicationInfo(packageName, 0)
        }

    private companion object {
        const val ICON_DP = 64
    }
}
