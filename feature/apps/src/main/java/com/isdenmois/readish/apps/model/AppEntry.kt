package com.isdenmois.readish.apps.model

import android.graphics.Bitmap

data class AppEntry(val packageName: String, val label: String, val icon: Bitmap?)

const val APPS_PAGE_SIZE = 20
