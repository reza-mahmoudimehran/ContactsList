package ir.reza_mahmoudi.contactslist.feature_contacts.data.repository

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.feature_contacts.data.local.ContactsDao
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ContactsRepository {
    override fun getContactsList(): Flow<List<ContactEntity>> = contactsDao.getContactsList()
}