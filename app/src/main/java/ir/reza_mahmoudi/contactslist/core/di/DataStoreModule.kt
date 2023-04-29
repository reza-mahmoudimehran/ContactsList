package ir.reza_mahmoudi.contactslist.core.di

import android.content.Context
import androidx.datastore.preferences.createDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import ir.reza_mahmoudi.contactslist.core.data.data_store.DataStoreRepositoryImpl
import ir.reza_mahmoudi.contactslist.core.util.constant.AppConstants.DATA_STORE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    companion object{
        @Singleton
        @Provides
        fun provideDataStore(
            @ApplicationContext context: Context
        ) = context.createDataStore(DATA_STORE_NAME)
    }

    @Singleton
    @Binds
    abstract fun provideDataStoreRepository(dataStoreRepositoryImpl: DataStoreRepositoryImpl): DataStoreRepository
}