package com.pperotti.android.sparq.demoapp.data.items

import com.pperotti.android.sparq.demoapp.data.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Operations exposed via the remote data source via its API.
 */
interface ItemRemoteDataSource {

    /**
     * Retrieve the list of items from the remote API
     *
     * @return ItemListResult The result of the operation encapsulated in a [ItemListResult] object
     */
    suspend fun retrieveItemList() : List<RemoteItem>

}

@Singleton
class DefaultItemRemoteDataSource @Inject constructor(
    val itemApi: ItemApi
) : ItemRemoteDataSource {
    override suspend fun retrieveItemList(): List<RemoteItem> {
        return itemApi.fetchItemList()
    }
}
