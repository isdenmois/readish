package com.isdenmois.readish.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class PageTest {
    @Test
    fun page_returnsPageSizeItems() {
        // Arrange
        val items = (1..25).map { "item-$it" }

        // Act
        val page = items.page(page = 0, pageSize = PAGE_SIZE)

        // Assert
        assertEquals(PAGE_SIZE, page.items.size)
        assertEquals(2, page.pageCount)
        assertEquals(0, page.page)
    }

    @Test
    fun page_returnsRemainderOnLastPage() {
        // Arrange
        val items = (1..25).map { "item-$it" }

        // Act
        val page = items.page(page = 1, pageSize = PAGE_SIZE)

        // Assert
        assertEquals(5, page.items.size)
        assertEquals(1, page.page)
        assertEquals("item-21", page.items.first())
    }

    @Test
    fun page_clampsPageAboveLastPage() {
        // Arrange
        val items = (1..25).map { "item-$it" }

        // Act
        val page = items.page(page = 99, pageSize = PAGE_SIZE)

        // Assert
        assertEquals(1, page.page)
        assertEquals(5, page.items.size)
    }

    @Test
    fun page_clampsNegativePage() {
        // Arrange
        val items = (1..25).map { "item-$it" }

        // Act
        val page = items.page(page = -3, pageSize = PAGE_SIZE)

        // Assert
        assertEquals(0, page.page)
        assertEquals(PAGE_SIZE, page.items.size)
    }

    @Test
    fun page_ofEmptyList_isSingleEmptyPage() {
        // Act
        val page = emptyList<String>().page(page = 4, pageSize = PAGE_SIZE)

        // Assert
        assertEquals(emptyList<String>(), page.items)
        assertEquals(0, page.page)
        assertEquals(1, page.pageCount)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
