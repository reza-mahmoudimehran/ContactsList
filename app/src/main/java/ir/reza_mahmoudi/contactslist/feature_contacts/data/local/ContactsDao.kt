package ir.reza_mahmoudi.contactslist.feature_contacts.data.local

import androidx.room.Dao
import androidx.room.Query
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactsDao {
    @Query("SELECT * FROM $CONTACTS_TABLE_NAME")
    fun getContactsList(): Flow<List<ContactEntity>>
}