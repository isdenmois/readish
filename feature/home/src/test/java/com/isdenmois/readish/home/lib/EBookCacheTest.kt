package com.isdenmois.readish.home.lib

import android.content.Context
import android.content.SharedPreferences
import com.isdenmois.ebookparser.EBookFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class EBookCacheTest {
    private lateinit var preferences: SharedPreferences
    private lateinit var cache: EBookCache

    @Before
    fun setUp() {
        preferences = RuntimeEnvironment.getApplication().getSharedPreferences("test", Context.MODE_PRIVATE)
        cache = EBookCache(CacheManager(preferences))
    }

    @Test
    fun set_thenGet_returnsBookFromMemory() {
        // Arrange
        val file = File("/books/dune.fb2")
        val book = EBookFile(title = "Dune", author = "Herbert", file = file, cover = null)

        // Act
        cache.set(book)
        val cached = cache[file]

        // Assert
        assertEquals("Dune", cached?.title)
        assertEquals("Herbert", cached?.author)
    }

    @Test
    fun get_restoresBookFromPreferencesWhenMemoryIsCold() {
        // Arrange
        val file = File("/books/neuromancer.fb2")
        preferences.edit().putString(file.name, "Neuromancer||Gibson").commit()

        // Act
        val cached = cache[file]

        // Assert
        assertEquals("Neuromancer", cached?.title)
        assertEquals("Gibson", cached?.author)
    }

    @Test
    fun get_returnsNullWhenNothingIsCached() {
        // Act
        val cached = cache[File("/books/unknown.fb2")]

        // Assert
        assertNull(cached)
    }

    @Test
    fun get_ignoresCorruptedCacheEntry() {
        // Arrange
        val file = File("/books/broken.fb2")
        preferences.edit().putString(file.name, "title-without-separator").commit()

        // Act
        val cached = cache[file]

        // Assert
        assertNull(cached)
    }
}
