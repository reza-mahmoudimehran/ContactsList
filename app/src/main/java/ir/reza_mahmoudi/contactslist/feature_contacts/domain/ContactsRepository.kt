package ir.reza_mahmoudi.contactslist.feature_contacts.domain

import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun getContactsList(): Flow<List<ContactEntity>>

    suspend fun addNewContacts(items: List<ContactEntity>)
}