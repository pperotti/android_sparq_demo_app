package com.pperotti.android.sparq.demoapp.data.items

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

/**
 * An Item representation as exposed by the remote API
 */
data class RemoteItem(
    val title: String?,
    val description: String?
)

/**
 * Mapper between RemoteItem and Item
 *
 * @param remoteItem The RemoteItem to transform
 * @return The
 */
fun transformRemoteItemIntoItem(remoteItem: RemoteItem): Item {
    return Item(
        title = remoteItem.title,
        description = remoteItem.description
    )
}