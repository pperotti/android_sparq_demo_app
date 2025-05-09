package com.pperotti.android.sparq.demoapp.data.items

import javax.inject.Singleton

interface ItemLocalDataSource {

    suspend fun getItemList(): ItemListResult

    suspend fun getItemDetail(id: Int): ItemResult

    suspend fun saveItemListResult(remoteItemList: List<RemoteItem>)

    suspend fun hasItemListResult(): Boolean
}

@Singleton
class DefaultItemLocalDataSource(

) : ItemLocalDataSource {

    var lastResult = ItemListResult(emptyList())

    override suspend fun getItemList(): ItemListResult {
        return lastResult
    }

    override suspend fun getItemDetail(id: Int): ItemResult {
        TODO("Not yet implemented")
    }

    override suspend fun saveItemListResult(remoteItemList: List<RemoteItem>) {
        lastResult = ItemListResult(remoteItemList.map {
            transformRemoteItemIntoItem(it)
        })
    }

    override suspend fun hasItemListResult(): Boolean {
        return false
    }
}
