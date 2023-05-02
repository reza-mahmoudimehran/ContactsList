package ir.reza_mahmoudi.contactslist.feature_contacts.domain

import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getContactsList(): Flow<List<ContactEntity>>

    suspend fun deleteContactsByNumbers(itemsNumber: List<String>)

    suspend fun addNewContacts(items: List<ContactEntity>)

    suspend fun deleteAndInsertContacts(
        contactsToDelete: List<String>,
        contactsToAdd: List<ContactEntity>
    )
}