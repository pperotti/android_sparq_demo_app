package com.pperotti.android.sparq.demoapp.data.items

import javax.inject.Singleton

interface ItemLocalDataSource {

    suspend fun getItemList(): ItemListResult

    suspend fun saveItemListResult(remoteItemList: List<RemoteItem>)

    suspend fun hasItemListResult(): Boolean
}

@Singleton
class DefaultItemLocalDataSource(
    val itemDao: ItemDao
) : ItemLocalDataSource {

    override suspend fun getItemList(): ItemListResult {
        return ItemListResult(
            items = itemDao.getAllItems().map {
                it.toItem()
            }
        )
    }

    override suspend fun saveItemListResult(remoteItemList: List<RemoteItem>) {
        itemDao.insertAll(remoteItemList.map {
            it.toStorageItem()
        })
    }

    override suspend fun hasItemListResult(): Boolean {
        return itemDao.getItemCount() > 0
    }
}
