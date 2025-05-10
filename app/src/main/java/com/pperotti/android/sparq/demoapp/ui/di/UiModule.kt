package com.pperotti.android.sparq.demoapp.ui.di

import com.pperotti.android.sparq.demoapp.data.items.ItemRepository
import com.pperotti.android.sparq.demoapp.ui.main.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UiModule {
    @Provides
    @Singleton
    fun provideMainViewModel(itemRepository: ItemRepository): MainViewModel {
        return MainViewModel(itemRepository)
    }
}
