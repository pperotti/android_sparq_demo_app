package com.pperotti.android.sparq.demoapp.data.items


import com.google.gson.JsonSyntaxException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException

class DefaultItemRemoteDataSourceTest {

    lateinit var subject: DefaultItemRemoteDataSource

    @RelaxedMockK
    lateinit var mockItemApi: ItemApi

    @Before
    fun setUp() {
        // Initialize all annotated mocks
        MockKAnnotations.init(this)

        // Initialize subject to test
        subject = DefaultItemRemoteDataSource(mockItemApi)
    }

    @Test
    fun `retrieveItemList successful response`() = runBlocking {
        val expectedItems = listOf(
            RemoteItem("Item 1", "Description 1"),
            RemoteItem("Item 2", "Description 2")
        )
        coEvery { mockItemApi.fetchItemList() } returns expectedItems

        val actualItems = subject.retrieveItemList()
        assertEquals(expectedItems, actualItems)
    }

    @Test
    fun `retrieveItemList empty list response`() = runBlocking {
        val expectedItems = emptyList<RemoteItem>()
        coEvery { mockItemApi.fetchItemList() } returns expectedItems

        val actualItems = subject.retrieveItemList()
        assertEquals(expectedItems, actualItems)
    }

    @Test
    fun `retrieveItemList network error`() = runBlocking {
        val networkException = IOException("Network error")
        coEvery { mockItemApi.fetchItemList() } throws networkException

        val exception =
            assertThrows(IOException::class.java) { runBlocking { subject.retrieveItemList() } }
        assertEquals(networkException.message, exception.message)
    }

    @Test
    fun `retrieveItemList server error`() = runBlocking {
        // Mock a server error (e.g., 500 Internal Server Error)
        val serverException = mockk<HttpException>()
        coEvery { mockItemApi.fetchItemList() } throws serverException

        val exception =
            assertThrows(HttpException::class.java) { runBlocking { subject.retrieveItemList() } }
        assertEquals(serverException, exception)
    }

    @Test
    fun `retrieveItemList malformed response`() = runBlocking {
        // Simulate a response that cannot be parsed into List<RemoteItem>
        val malformedResponseException = JsonSyntaxException("Malformed JSON")
        coEvery { mockItemApi.fetchItemList() } throws malformedResponseException

        val exception =
            assertThrows(JsonSyntaxException::class.java) { runBlocking { subject.retrieveItemList() } }
        assertEquals(malformedResponseException.message, exception.message)
    }

    @Test
    fun `retrieveItemList cancellation`() = runBlocking {
        // Simulate cancellation of the coroutine
        coEvery { mockItemApi.fetchItemList() } coAnswers { throw CancellationException("Job was cancelled") }

        // Verify that retrieveItemList throws CancellationException
        assertThrows(CancellationException::class.java) { runBlocking { subject.retrieveItemList() } }
        coVerify { mockItemApi.fetchItemList() }
    }

    @Test
    fun `retrieveItemList timeout`() = runBlocking {
        // Simulate a timeout exception
        val timeoutException = TimeoutException("Request timed out")
        coEvery { mockItemApi.fetchItemList() } throws timeoutException

        val exception =
            assertThrows(TimeoutException::class.java) { runBlocking { subject.retrieveItemList() } }
        assertEquals(timeoutException.message, exception.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `retrieveItemList concurrent calls`() = runBlocking {
        val expectedItems = listOf(RemoteItem("Item 1", "Description 1"))
        coEvery { mockItemApi.fetchItemList() } returns expectedItems

        // Launch multiple coroutines to call retrieveItemList concurrently
        val job1 = launch(Dispatchers.IO) { // Use Dispatchers.IO or a test dispatcher
            val items = subject.retrieveItemList()
            assertEquals(expectedItems, items)
        }
        val job2 = launch(Dispatchers.IO) {
            val items = subject.retrieveItemList()
            assertEquals(expectedItems, items)
        }

        job1.join()
        job2.join()

        // Verify that fetchItemList was called, ideally twice if the implementation doesn't cache or deduplicate calls
        // For a simple stateless call as in the current DefaultItemRemoteDataSource, it would be called for each invocation.
        // If there was caching, this verification would change.
        coVerify(atLeast = 1) { mockItemApi.fetchItemList() } // atLeast = 1 because the exact number depends on test dispatcher behavior and potential optimizations.
        // If strict concurrency guarantees are needed, one might need more sophisticated tools or assertions.
    }

}
