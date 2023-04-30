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
    @Query("SELECT * FROM $CONTACTS_TABLE_NAME")
    suspend fun getContactsList(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewContacts(items: List<ContactEntity>)
}