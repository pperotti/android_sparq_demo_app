package com.pperotti.android.sparq.demoapp.data.items

import com.pperotti.android.sparq.demoapp.data.common.DataResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Defines the supported operations by for the "items" api.
 */
interface ItemRepository {
    suspend fun fetchItemList(): DataResponse<ItemListResult>
}

@Singleton
class DefaultItemRepository @Inject constructor(
    val localDataSource: ItemLocalDataSource,
    val remoteDataSource: ItemRemoteDataSource,
    val dispatcher: CoroutineDispatcher
) : ItemRepository {

    override suspend fun fetchItemList(): DataResponse<ItemListResult> {
        return withContext(dispatcher) {
            try {
                if (!localDataSource.hasItemListResult()) {
                    val remoteList = remoteDataSource.retrieveItemList()
                    localDataSource.saveItemListResult(remoteList)
                }
                DataResponse.Success(localDataSource.getItemList())
            } catch (e: Exception) {
                e.printStackTrace()
                DataResponse.Error(e.localizedMessage, e.cause)
            }
        }
    }
}
