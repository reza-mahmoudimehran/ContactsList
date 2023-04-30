package ir.reza_mahmoudi.contactslist.feature_contacts.data.repository

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.feature_contacts.data.local.ContactsDao
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ContactsRepository {
    override suspend fun getContactsList(): Flow<List<ContactEntity>> = withContext(ioDispatcher) {
        contactsDao.getContactsList()
    }

    override suspend fun addNewContacts(items: List<ContactEntity>) = withContext(ioDispatcher) {
        contactsDao.addNewContacts(items)
    }
}