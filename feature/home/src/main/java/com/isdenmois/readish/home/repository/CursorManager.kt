package com.isdenmois.readish.home.repository

import android.database.Cursor
import java.io.File

object CursorManager {
    fun getInt(cursor: Cursor, column: String): Int {
        val index = cursor.getColumnIndex(column)

        if (index == -1 || cursor.isNull(index)) {
            return 0
        }

        return cursor.getInt(index)
    }

    fun getString(cursor: Cursor, column: String, defaultValue: String = ""): String {
        val index = cursor.getColumnIndex(column)

        if (index == -1 || cursor.isNull(index)) {
            return defaultValue
        }

        return cursor.getString(index)
    }

    fun get_data(cursor: Cursor): String? {
        val index = cursor.getColumnIndex("_data")

        if (index == -1 || cursor.isNull(index)) {
            return null
        }

        return cursor.getString(index)
    }

    fun getFile(cursor: Cursor, column: String): File {
        val path = getString(cursor, column)
        val index = path.indexOf('')

        if (index > 0) {
            return File(path.substring(0, index))
        }

        return File(path)
    }
}
