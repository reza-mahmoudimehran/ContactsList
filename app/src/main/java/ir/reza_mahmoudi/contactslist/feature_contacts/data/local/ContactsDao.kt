package ir.reza_mahmoudi.contactslist.feature_contacts.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactsDao {
    @Query("SELECT * FROM $CONTACTS_TABLE_NAME ORDER BY name")
    fun getContactsList(): Flow<List<ContactEntity>>

    @Query("DELETE FROM $CONTACTS_TABLE_NAME WHERE contact_id IN (:itemIds)")
    fun deleteContactsByIds(itemIds: List<Long>)

    @Query("DELETE FROM $CONTACTS_TABLE_NAME WHERE phone IN (:itemsNumber)")
    fun deleteContactsByNumbers(itemsNumber: List<String>)

//    @Query("DELETE FROM $CONTACTS_TABLE_NAME")
//    fun deleteContactsByNumbers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewContacts(items: List<ContactEntity>)
}