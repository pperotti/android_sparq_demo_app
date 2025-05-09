package com.pperotti.android.sparq.demoapp.data.items

/**
 * All items available here represents the elements exposed by "Data" layer
 * to those using them.
 */

/**
 * Represent the result of a list of items and the page number.
 */
data class ItemListResult(
    val items: List<Item>
)

/**
 * Represent an item in the list.
 */
data class Item(
    val id: Int = 0,
    val title: String?,
    val description: String?
)

/**
 * Result exposed by the data layer regardless the source of the information
 */
data class ItemResult(
    val id: Int,
    val item: Item
)

/**
 * An Item representation as exposed by the remote API
 */
data class RemoteItem(
    val title: String?,
    val description: String?
)

/**
 * Mapper between RemoteItem and Item
 */
fun transformRemoteItemIntoItem(remoteItem: RemoteItem): Item {
    return Item(
        title = remoteItem.title,
        description = remoteItem.description
    )
}
