package com.isdenmois.readish.home.lib

import android.database.MatrixCursor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class CursorExtensionsTest {
    @Test
    fun typedGetters_readColumnsByName() {
        // Arrange
        val cursor = MatrixCursor(arrayOf("title", "size")).apply { addRow(arrayOf("Dune", 42)) }
        cursor.moveToFirst()

        // Act & Assert
        assertEquals("Dune", cursor.getString("title"))
        assertEquals(42, cursor.getInt("size"))
    }

    @Test
    fun getFile_stripsReaderSpecificSuffix() {
        // Arrange
        val cursor = MatrixCursor(arrayOf("filename")).apply { addRow(arrayOf("/books/dune.fb2\u0001extra")) }
        cursor.moveToFirst()

        // Act
        val file = cursor.getFile("filename")

        // Assert
        assertEquals(File("/books/dune.fb2"), file)
    }

    @Test
    fun getFile_keepsPlainPath() {
        // Arrange
        val cursor = MatrixCursor(arrayOf("filename")).apply { addRow(arrayOf("/books/dune.fb2")) }
        cursor.moveToFirst()

        // Act
        val file = cursor.getFile("filename")

        // Assert
        assertEquals(File("/books/dune.fb2"), file)
    }

    @Test
    fun forEach_visitsEveryRow() {
        // Arrange
        val cursor =
            MatrixCursor(arrayOf("title")).apply {
                addRow(arrayOf("Dune"))
                addRow(arrayOf("Neuromancer"))
            }
        val titles = mutableListOf<String>()

        // Act
        cursor.forEach { titles.add(getString("title")) }

        // Assert
        assertEquals(listOf("Dune", "Neuromancer"), titles)
    }

    @Test
    fun forEach_doesNotVisitAnythingForEmptyCursor() {
        // Arrange
        val cursor = MatrixCursor(arrayOf("title"))
        var visits = 0

        // Act
        cursor.forEach { visits++ }

        // Assert
        assertEquals(0, visits)
    }
}
