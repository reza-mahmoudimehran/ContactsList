package ir.reza_mahmoudi.contactslist.feature_contacts.di

import android.app.Application
import android.content.ContentResolver
import android.os.Handler
import android.os.Looper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.reza_mahmoudi.contactslist.core.data.local.ContactsListDatabase
import ir.reza_mahmoudi.contactslist.feature_contacts.data.repository.ContactsRepositoryImpl
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {

    companion object {
        @Singleton
        @Provides
        fun provideContactsDao(database: ContactsListDatabase) = database.getContactsDao()

        @ContactObserverHandler
        @Singleton
        @Provides
        fun provideHandler(): Handler = Handler(Looper.getMainLooper())

        @Singleton
        @Provides
        fun provideContentResolver(application: Application): ContentResolver =
            application.contentResolver

        @ContactObserverCoroutineScope
        @Provides
        fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    }

    @Singleton
    @Binds
    abstract fun providesContactsRepository(contactsRepositoryImpl: ContactsRepositoryImpl): ContactsRepository

}