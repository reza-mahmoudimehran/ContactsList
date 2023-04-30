package ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME


@Entity(
    tableName = CONTACTS_TABLE_NAME
)
data class ContactEntity(
//  @ColumnInfo(name = "id") val id: Int = 0,
    @PrimaryKey @ColumnInfo(name = "contact_id") val contactId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") var phone: String = "",
)