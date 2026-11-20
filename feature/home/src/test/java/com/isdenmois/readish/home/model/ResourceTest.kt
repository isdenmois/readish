package com.isdenmois.readish.home.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResourceTest {
    @Test
    fun success_holdsDataWithoutMessage() {
        // Act
        val resource = Resource.success("data")

        // Assert
        assertEquals(Status.SUCCESS, resource.status)
        assertEquals("data", resource.data)
        assertNull(resource.message)
    }

    @Test
    fun error_holdsMessageWithoutData() {
        // Act
        val resource = Resource.error<Int>(message = "boom")

        // Assert
        assertEquals(Status.ERROR, resource.status)
        assertNull(resource.data)
        assertEquals("boom", resource.message)
    }

    @Test
    fun loading_canKeepPreviousData() {
        // Act
        val resource = Resource.loading(listOf(1, 2))

        // Assert
        assertEquals(Status.LOADING, resource.status)
        assertEquals(listOf(1, 2), resource.data)
        assertNull(resource.message)
    }
}
