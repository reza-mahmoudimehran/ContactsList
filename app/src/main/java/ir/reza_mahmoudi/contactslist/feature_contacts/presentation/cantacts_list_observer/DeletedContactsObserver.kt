package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.util.log.showLog
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import javax.inject.Inject

class DeletedContactsObserver @Inject constructor(
    private val contentResolver: ContentResolver,
    @ContactObserverHandler private val handler: Handler
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val contentResolver = contentResolver
        val deletedContactsUri = ContactsContract.DeletedContacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.DeletedContacts.CONTACT_ID)

        val cursor: Cursor? = contentResolver.query(
            deletedContactsUri,
            projection,
            null,
            null,
            null
        )

        cursor?.use { data ->
            val deletedIndex = data.getColumnIndex(ContactsContract.DeletedContacts.CONTACT_ID)
            val timeIndex = data.getColumnIndex(ContactsContract.DeletedContacts.CONTACT_DELETED_TIMESTAMP)
            while (data.moveToNext()) {
                val contactId = data.getLong(deletedIndex)
                showLog(tag="deleted id ",msg = "$timeIndex $contactId")
            }
        }
    }

    fun register() {
        val contentResolver = contentResolver
        contentResolver.registerContentObserver(
            ContactsContract.DeletedContacts.CONTENT_URI,
            true,
            this
        )
    }

    fun unregister() {
        val contentResolver = contentResolver
        contentResolver.unregisterContentObserver(this)
    }
}