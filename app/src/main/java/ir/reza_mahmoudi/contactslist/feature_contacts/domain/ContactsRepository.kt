package ir.reza_mahmoudi.contactslist.feature_contacts.domain

import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getContactsList(): Flow<List<ContactEntity>>

    fun deleteContactsByIds(itemsNumber: List<Long>)

    fun deleteContactsByNumbers(itemsNumber: List<String>)

    fun addNewContacts(items: List<ContactEntity>)
}