package com.isdenmois.readish.library.lib

import com.isdenmois.ebookparser.BitmapDecoder
import com.isdenmois.ebookparser.EBookFile
import java.io.File
import javax.inject.Inject

class EBookCache
@Inject
constructor(private val cacheManager: CacheManager) {
    private val memoryCache = HashMap<String, EBookFile>()

    operator fun get(file: File): EBookFile? {
        val key = file.name

        if (memoryCache.containsKey(key)) {
            return memoryCache[key]
        }
        val cached = cacheManager.get(key)

        cached?.let {
            return fromCache(file, it)
        }

        return null
    }

    fun set(book: EBookFile?) {
        book?.let {
            val key = book.file.name

            memoryCache[key] = book
            cacheManager.set(key, book.toCache())
        }
    }

    private fun EBookFile.toCache(): String = "$title||$author"

    private fun fromCache(file: File, value: String): EBookFile? {
        val parts = value.split("||", limit = 2)

        if (parts.size != 2) return null

        val (title, author) = parts

        return EBookFile(
            title = title,
            author = author,
            file = file,
            cover = BitmapDecoder.fromFile(file.name),
        )
    }
}
