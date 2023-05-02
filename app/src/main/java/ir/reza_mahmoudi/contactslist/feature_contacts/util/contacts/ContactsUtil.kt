package ir.reza_mahmoudi.contactslist.feature_contacts.util.contacts

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactsPhoneNumberMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun getChangedContactsNames(
    dataStoreRepository: DataStoreRepository,
    contentResolver: ContentResolver,
    ioDispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    lastTimestamp: Long,
): List<ContactEntity> {
    val contactsList = ArrayList<ContactEntity>()

    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
    )

    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " > $lastTimestamp",
        null,
        "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"
    )
    var newTimestamp: Long = 0
    cursor?.use { contactsCursor ->
        val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
        val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val timeIndex =
            contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)
        if (contactsCursor.moveToFirst()) {
            newTimestamp = contactsCursor.getLong(timeIndex)
            do {
                val id = contactsCursor.getLong(idIndex)
                val name = contactsCursor.getString(nameIndex)

                if (name != null) {
                    contactsList.add(ContactEntity(id, name))
                }
            } while (contactsCursor.moveToNext())
        }
    }
    coroutineScope.launch(ioDispatcher) {
        dataStoreRepository.save(
            PreferencesKeys.contactsLastTimestamp,
            newTimestamp
        )
    }
    return contactsList
}


fun getAllContactsNames(
    dataStoreRepository: DataStoreRepository,
    contentResolver: ContentResolver,
    ioDispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
): List<ContactEntity> {
    val contactsList = ArrayList<ContactEntity>()

    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
    )

    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        null,
        null,
        "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"
    )
    var newTimestamp: Long = 0

    cursor?.use { contactsCursor ->
        val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
        val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val timeIndex =
            contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)

        if (contactsCursor.moveToFirst()) {
            newTimestamp = contactsCursor.getLong(timeIndex)
            do {
                val id = contactsCursor.getLong(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(ContactEntity(id, name))
                }
            } while (contactsCursor.moveToNext())
        }
    }
    coroutineScope.launch(ioDispatcher) {
        dataStoreRepository.save(
            PreferencesKeys.contactsLastTimestamp,
            newTimestamp
        )
    }
    return contactsList
}

fun getContactNumbers(
    contentResolver: ContentResolver,
): ContactsPhoneNumberMap {
    val contactsNumberSet = HashSet<String>()
    val contactsNumberMap = HashMap<Long, ArrayList<String>>()

    val cursor: Cursor? = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
    cursor?.use { contactsCursor ->
        val contactIdIndex =
            contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val numberIndex =
            contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        if (contactsCursor.moveToFirst()) {
            do {
                val contactId = contactsCursor.getLong(contactIdIndex)
                val number: String = contactsCursor.getString(numberIndex).replace(" ", "")

                contactsNumberSet.add(number)

                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            } while (contactsCursor.moveToNext())
        }
    }
    return ContactsPhoneNumberMap(contactsNumberSet, contactsNumberMap)
}