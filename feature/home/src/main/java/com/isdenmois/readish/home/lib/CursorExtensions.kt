package com.isdenmois.readish.home.lib

import android.database.Cursor
import java.io.File

fun Cursor.column(which: String) = getColumnIndex(which)
fun Cursor.getInt(which: String): Int = getInt(column(which))
fun Cursor.getString(which: String): String = getString(column(which))

fun Cursor.getFile(column: String): File {
    val path = getString(column)
    val index = path.indexOf('')

    if (index > 0) {
        return File(path.substring(0, index))
    }

    return File(path)
}

inline fun Cursor.forEach(action: Cursor.() -> Unit) {
    moveToFirst()

    while (!isAfterLast) {
        action()
        moveToNext()
    }
}
