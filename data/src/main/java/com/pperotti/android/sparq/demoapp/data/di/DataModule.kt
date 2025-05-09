package com.pperotti.android.sparq.demoapp.data.di

import com.pperotti.android.sparq.demoapp.data.items.DefaultItemLocalDataSource
import com.pperotti.android.sparq.demoapp.data.items.DefaultItemRemoteDataSource
import com.pperotti.android.sparq.demoapp.data.items.DefaultItemRepository
import com.pperotti.android.sparq.demoapp.data.items.ItemApi
import com.pperotti.android.sparq.demoapp.data.items.ItemLocalDataSource
import com.pperotti.android.sparq.demoapp.data.items.ItemRemoteDataSource
import com.pperotti.android.sparq.demoapp.data.items.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

//    @Provides
//    @Singleton
//    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
//        return Room.databaseBuilder(
//            context,
//            MovieDatabase::class.java,
//            "movies_database"
//        ).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideMovieDao(database: MovieDatabase): MovieDao {
//        return database.movieDao()
//    }
//

    @Provides
    @Singleton
    fun provideItemLocalDataSource(/*itemDao: ItemDao*/): ItemLocalDataSource {
        return DefaultItemLocalDataSource(/*itemDao*/)
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
