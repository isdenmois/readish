package com.isdenmois.readish.ui

import android.content.Context
import android.widget.Toast

class AppLauncher(private val context: Context) {
    fun launch(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)

        if (intent == null) {
            showCantStart()
            return
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showCantStart()
        }
    }

    private fun showCantStart() {
        Toast.makeText(context, context.getString(R.string.cant_start_application), Toast.LENGTH_SHORT).show()
    }
}
