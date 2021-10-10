package com.isdenmois.readish.shared.api.alreader

import android.database.Cursor

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

    fun getPath(cursor: Cursor, column: String): String {
        val path = getString(cursor, column)
        val index = path.indexOf('')

        if (index > 0) {
            return path.substring(0, index)
        }

        return path
    }
}
