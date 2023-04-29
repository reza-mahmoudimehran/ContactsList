package ir.reza_mahmoudi.contactslist.feature_contacts.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.reza_mahmoudi.contactslist.core.data.local.ContactsListDatabase
import ir.reza_mahmoudi.contactslist.feature_contacts.data.repository.ContactsRepositoryImpl
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {

    companion object {
        @Singleton
        @Provides
        fun provideContactsDao(database: ContactsListDatabase) = database.getContactsDao()
    }

    @Singleton
    @Binds
    abstract fun providesContactsRepository(contactsRepositoryImpl: ContactsRepositoryImpl): ContactsRepository

}