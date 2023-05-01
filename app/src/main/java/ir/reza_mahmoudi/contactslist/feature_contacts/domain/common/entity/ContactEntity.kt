package ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.reza_mahmoudi.contactslist.feature_contacts.util.constant.ContactsConstants.CONTACTS_TABLE_NAME


@Entity(
    tableName = CONTACTS_TABLE_NAME
)
data class ContactEntity(
    @ColumnInfo(name = "contact_id") val contactId: Long,
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey @ColumnInfo(name = "phone") var phone: String = "",
){
    override fun toString(): String = "$name -> $phone\n"
}