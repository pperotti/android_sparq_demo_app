package com.pperotti.android.sparq.demoapp.data.items

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class DefaultItemLocalDataSourceTest {

    lateinit var subject: DefaultItemLocalDataSource

    @RelaxedMockK
    lateinit var mockItemDao: ItemDao

    @Before
    fun setUp() {

        // Initialize all annotated mocks
        MockKAnnotations.init(this)

        // Initialize the subject with the mock itemDao
        subject = DefaultItemLocalDataSource(mockItemDao)
    }

    @Test
    fun `getItemList   Empty Database`() = runBlocking {
        // Given an empty database (mock DAO returns an empty list)
        coEvery { mockItemDao.getAllItems() } returns emptyList()

        // When getItemList is called
        val result = subject.getItemList()

        // Then the result should be an empty ItemListResult
        assertEquals(ItemListResult(emptyList()), result)
    }

    @Test
    fun `getItemList   Non Empty Database`() = runBlocking {
        // Given a non-empty database (mock DAO returns a list of StorageItems)
        val storageItems = listOf(
            StorageItem(1, "Item 1", "Description 1"),
            StorageItem(2, "Item 2", "Description 2")
        )
        coEvery { mockItemDao.getAllItems() } returns storageItems

        // When getItemList is called
        val result = subject.getItemList()

        // Then the result should be an ItemListResult with correctly mapped items
        val expectedItems = storageItems.map { it.toItem() }
        val expectedResult = ItemListResult(expectedItems)
        assertEquals(expectedResult, result)
        assertEquals(expectedItems.size, result.items.size)
        assertEquals(expectedItems[0].title, result.items[0].title)
        assertEquals(expectedItems[1].description, result.items[1].description)
    }

    @Test
    fun `getItemList   Mapping Verification`() = runBlocking {
        // Given
        val storageItems = listOf(
            StorageItem(1, "Test Item 1", "Desc 1"),
            StorageItem(2, "Test Item 2", "Desc 2")
        )
        coEvery { mockItemDao.getAllItems() } returns storageItems

        // When
        val result = subject.getItemList()

        // Then
        assertEquals(storageItems.size, result.items.size)
        storageItems.forEachIndexed { index, storageItem ->
            val expectedItem = storageItem.toItem()
            val actualItem = result.items[index]
            assertEquals(expectedItem.id, actualItem.id)
            assertEquals(expectedItem.title, actualItem.title)
            assertEquals(expectedItem.description, actualItem.description)
        }
    }

    @Test
    fun `getItemList   Database Error`() = runBlocking {
        // Given a database error (mock DAO throws an exception)
        val databaseException = RuntimeException("Database error")
        coEvery { mockItemDao.getAllItems() } throws databaseException

        // When getItemList is called
        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { subject.getItemList() }
        }

        // Then the exception should be propagated
        assertEquals(databaseException, exception)
    }

    @Test
    fun `getItemList   Concurrency`() = runBlocking {
        // Given
        val storageItems = listOf(
            StorageItem(1, "Item 1", "Description 1"),
            StorageItem(2, "Item 2", "Description 2")
        )
        val expectedItems = storageItems.map { it.toItem() }
        val expectedResult = ItemListResult(expectedItems)

        // Mock DAO to return the same list consistently
        coEvery { mockItemDao.getAllItems() } returns storageItems

        val numConcurrentRequests = 10
        val deferredResults = (1..numConcurrentRequests).map {
            async(Dispatchers.IO) { // Use Dispatchers.IO for simulating background work
                subject.getItemList()
            }
        }

        val results = deferredResults.awaitAll()

        // Verify all results are consistent
        results.forEach { result ->
            assertEquals(expectedResult, result)
        }
        assertEquals(numConcurrentRequests, results.size)
    }

    @Test
    fun `saveItemListResult   Empty List`() = runBlocking {

        // When saveItemListResult is called with an empty list
        subject.saveItemListResult(emptyList())

        // Then no items should be inserted into the database
        coVerify(exactly = 1) { mockItemDao.insertAll(any()) }
    }

    @Test
    fun `saveItemListResult   Non Empty List`() = runBlocking {
        // Given a non-empty list of RemoteItems
        val remoteItems = listOf(
            RemoteItem("Remote Item 1", "Remote Desc 1"),
            RemoteItem("Remote Item 2", "Remote Desc 2")
        )

        // When saveItemListResult is called
        subject.saveItemListResult(remoteItems)

        // Then the correct StorageItems should be inserted into the database
        val expectedStorageItems = remoteItems.map { it.toStorageItem() }
        coVerify { mockItemDao.insertAll(expectedStorageItems) }

        // Additionally, verify the content of the list passed to insertAll
        coVerify { mockItemDao.insertAll(match { it.size == 2 && it[0].title == "Remote Item 1" && it[1].description == "Remote Desc 2" }) }
    }


    @Test
    fun `saveItemListResult   Mapping Verification`() = runBlocking {
        // Given
        val remoteItems = listOf(
            RemoteItem("Title1", "Desc1"),
            RemoteItem("Title2", "Desc2")
        )
        val expectedStorageItems = remoteItems.mapIndexed { index, item ->
            StorageItem(index, title = item.title, description = item.description)
        }

        // When
        subject.saveItemListResult(remoteItems)

        // Then
        coVerify {
            mockItemDao.insertAll(match { insertedList ->
                insertedList.size == expectedStorageItems.size &&
                        insertedList.zip(expectedStorageItems).all { (inserted, expected) ->
                            inserted.title == expected.title && inserted.description == expected.description
                        }
            })
        }
    }

    @Test
    fun `saveItemListResult   Database Insertion Conflict Error`() = runBlocking {
        val remoteItems = listOf(
            RemoteItem("Remote Item 1", "Remote Desc 1")
        )
        val databaseException = RuntimeException("Database insertion error")
        coEvery { mockItemDao.insertAll(any()) } throws databaseException

        // When saveItemListResult is called and an error occurs
        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { subject.saveItemListResult(remoteItems) }
        }

        // Then the exception should be propagated
        assertEquals(databaseException, exception)
    }

    @Test
    fun `saveItemListResult   Large List`() = runBlocking {
        // Given a large list of RemoteItems (e.g., 1000 items)
        val largeRemoteItems = (1..1000).map {
            RemoteItem("Item $it", "Description $it")
        }

        // When saveItemListResult is called
        subject.saveItemListResult(largeRemoteItems)

        // Then verify that all items were attempted to be inserted
        // The primary check here is that the operation completes without error and the DAO is called with the large list.
        // Performance would typically be profiled, not asserted directly in a unit test unless specific thresholds are defined.
        val expectedStorageItems = largeRemoteItems.map { it.toStorageItem() }
        coVerify { mockItemDao.insertAll(expectedStorageItems) }
    }

    @Test
    fun `saveItemListResult   Duplicate Items in Input`() = runBlocking {
        // Given a list with duplicate RemoteItems
        val duplicateRemoteItem = RemoteItem("Duplicate Item", "Duplicate Desc")
        val remoteItems = listOf(
            duplicateRemoteItem,
            RemoteItem("Unique Item", "Unique Desc"),
            duplicateRemoteItem // Duplicate entry
        )

        // When saveItemListResult is called
        subject.saveItemListResult(remoteItems)

        // Then verify that the DAO's insertAll is called with the mapped StorageItems.
        // The exact behavior (replace, ignore) depends on the DAO's @Insert(onConflict = ...) strategy.
        // This test assumes the conversion happens correctly and the DAO handles the duplicates.
        val expectedStorageItems = remoteItems.map { it.toStorageItem() }
        coVerify { mockItemDao.insertAll(expectedStorageItems) }
    }

    @Test
    fun `saveItemListResult   Concurrency`() = runBlocking {
        // Given a list of RemoteItems
        val remoteItems = listOf(
            RemoteItem("Item 1", "Description 1"),
            RemoteItem("Item 2", "Description 2")
        )
        val expectedStorageItems = remoteItems.map { it.toStorageItem() }

        // Mock DAO to allow verification of calls
        coEvery { mockItemDao.insertAll(any()) } returns Unit // Assume successful insertion

        val numConcurrentSaves = 10
        val deferredSaves = (1..numConcurrentSaves).map {
            async(Dispatchers.IO) { // Use Dispatchers.IO for simulating background work
                subject.saveItemListResult(remoteItems)
            }
        }

        // Wait for all concurrent save operations to complete
        deferredSaves.awaitAll()

        // Verify that insertAll was called for each concurrent operation
        // This ensures that each save attempt was processed.
        // The atomicity and consistency are primarily responsibilities of the underlying database and DAO implementation,
        // but this test checks that the LocalDataSource correctly delegates these operations.
        // We expect insertAll to be called numConcurrentSaves times, each with the same set of items.
        coVerify(exactly = numConcurrentSaves) { mockItemDao.insertAll(expectedStorageItems) }
    }

    @Test
    fun `hasItemListResult   Empty Database`() = runBlocking {
        // Given an empty database (mock DAO returns 0 for item count)
        coEvery { mockItemDao.getItemCount() } returns 0

        // When hasItemListResult is called
        val result = subject.hasItemListResult()

        // Then the result should be false
        assertFalse(result)
    }

    @Test
    fun `hasItemListResult   Non Empty Database`() = runBlocking {
        // Given a non-empty database (mock DAO returns a count > 0)
        coEvery { mockItemDao.getItemCount() } returns 5 // Example: 5 items in the database

        // When hasItemListResult is called
        val result = subject.hasItemListResult()

        // Then the result should be true
        assertTrue(result)
    }

    @Test
    fun `hasItemListResult   Database Error`() = runBlocking {
        // Given a database error when checking item count
        val databaseException = RuntimeException("Database error on count")
        coEvery { mockItemDao.getItemCount() } throws databaseException

        // When hasItemListResult is called
        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { subject.hasItemListResult() }
        }

        // Then the exception should be propagated
        assertEquals(databaseException, exception)
    }
}
