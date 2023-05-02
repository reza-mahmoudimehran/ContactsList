package ir.reza_mahmoudi.contactslist.feature_contacts.data.local

import androidx.room.*
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactsDao {
    @Query("SELECT * FROM $CONTACTS_TABLE_NAME ORDER BY name")
    fun getContactsList(): Flow<List<ContactEntity>>

    @Query("DELETE FROM $CONTACTS_TABLE_NAME WHERE phone IN (:itemsNumber)")
    suspend fun deleteContactsByNumbers(itemsNumber: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewContacts(items: List<ContactEntity>)


    @Transaction
    suspend fun deleteAndInsertContacts(
        contactsToDelete: List<String>,
        contactsToAdd: List<ContactEntity>
    ) {
        deleteContactsByNumbers(contactsToDelete)
        addNewContacts(contactsToAdd)
    }
}