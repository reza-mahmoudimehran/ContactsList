package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.SaveDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ContactListObserver @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val saveDataStoreItemUseCase: SaveDataStoreItemUseCase<Long>,
    private val contentResolver: ContentResolver,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
    @ContactObserverHandler private val handler: Handler
) : ContentObserver(handler) {
    private val contactsUri: Uri = ContactsContract.Contacts.CONTENT_URI
    private val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
    )

    @SuppressLint("Range")
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        coroutineScope.launch {
            val contactsLastTimestampDeferred = coroutineScope.async {
                readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first() ?: 0
            }
            val lastTimestamp: Long = contactsLastTimestampDeferred.await()

            // Query the contact list for contacts updated since the last observation
            val selection =
                "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} > $lastTimestamp"
            Log.e("contact", "$lastTimestamp")

            val cursor: Cursor? = contentResolver.query(
                contactsUri,
                projection,
                selection,
                null,
                null
            )

            launch {
                cursor?.use { contactsCursor ->
                    if (contactsCursor.moveToFirst()) {
                        // Contact list has changed, handle the changes here
                        do {
                            val contactId =
                                contactsCursor.getLong(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))
                            val contactName =
                                contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                            Log.e("contact", "$contactId $contactName")
                            // Handle the contact details as per your requirements
                        } while (contactsCursor.moveToNext())
                    }
                }
            }
            launch {
                saveDataStoreItemUseCase(
                    SaveDataStoreItemUseCase.Params.create(
                        PreferencesKeys.contactsLastTimestamp,
                        System.currentTimeMillis()
                    )
                )
            }
        }
    }
}