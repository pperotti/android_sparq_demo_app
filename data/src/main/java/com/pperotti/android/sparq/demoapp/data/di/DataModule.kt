package com.pperotti.android.sparq.demoapp.data.di

import android.content.Context
import androidx.room.Room
import com.pperotti.android.sparq.demoapp.data.items.DefaultItemLocalDataSource
import com.pperotti.android.sparq.demoapp.data.items.DefaultItemRemoteDataSource
import com.pperotti.android.sparq.demoapp.data.items.DefaultItemRepository
import com.pperotti.android.sparq.demoapp.data.items.ItemApi
import com.pperotti.android.sparq.demoapp.data.items.ItemDao
import com.pperotti.android.sparq.demoapp.data.items.ItemDatabase
import com.pperotti.android.sparq.demoapp.data.items.ItemLocalDataSource
import com.pperotti.android.sparq.demoapp.data.items.ItemRemoteDataSource
import com.pperotti.android.sparq.demoapp.data.items.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideItemsDatabase(@ApplicationContext context: Context): ItemDatabase {
        return Room.databaseBuilder(
            context,
            ItemDatabase::class.java,
            "items_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideItemDao(database: ItemDatabase): ItemDao {
        return database.itemDao()
    }

    @Provides
    @Singleton
    fun provideItemLocalDataSource(itemDao: ItemDao): ItemLocalDataSource {
        return DefaultItemLocalDataSource(itemDao)
    }

    @Provides
    @Singleton
    fun provideItemRemoteDataSource(itemApi: ItemApi): ItemRemoteDataSource {
        return DefaultItemRemoteDataSource(itemApi)
    }

    @Provides
    @Singleton
    fun provideItemRepository(
        localDataSource: ItemLocalDataSource,
        remoteDataSource: ItemRemoteDataSource,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ItemRepository {
        return DefaultItemRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcher =  dispatcher
        )
    }
}
