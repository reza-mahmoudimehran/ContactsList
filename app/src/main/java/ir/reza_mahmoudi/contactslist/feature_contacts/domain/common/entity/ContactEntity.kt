package ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME


@Entity(
    tableName = CONTACTS_TABLE_NAME
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "contact_id") val contactId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String
)