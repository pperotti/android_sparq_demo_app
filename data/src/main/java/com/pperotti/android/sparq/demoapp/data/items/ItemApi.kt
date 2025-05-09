package com.pperotti.android.sparq.demoapp.data.items

import retrofit2.http.GET

/**
 * API associated with retrieving an item list from a remote source.
 */
interface ItemApi {

    @GET("lanestp/challenge-for-ios/refs/heads/main/test.json")
    suspend fun fetchItemList(): List<RemoteItem>

}
