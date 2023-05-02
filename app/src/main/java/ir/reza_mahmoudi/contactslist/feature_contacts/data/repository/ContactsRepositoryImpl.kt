package ir.reza_mahmoudi.contactslist.feature_contacts.data.repository

import ir.reza_mahmoudi.contactslist.feature_contacts.data.local.ContactsDao
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao
) : ContactsRepository {
    override fun getContactsList(): Flow<List<ContactEntity>> = contactsDao.getContactsList()

    override suspend fun deleteContactsByNumbers(itemsNumber: List<String>) = contactsDao.deleteContactsByNumbers(
        itemsNumber
    )

    override suspend fun addNewContacts(items: List<ContactEntity>) = contactsDao.addNewContacts(items)

    override suspend fun deleteAndInsertContacts(
        contactsToDelete: List<String>,
        contactsToAdd: List<ContactEntity>
    ) = contactsDao.deleteAndInsertContacts(contactsToDelete,contactsToAdd)
}