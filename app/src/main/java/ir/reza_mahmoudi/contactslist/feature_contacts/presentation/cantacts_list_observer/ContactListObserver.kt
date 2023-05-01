package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.SaveDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.util.log.showLog
import ir.reza_mahmoudi.contactslist.core.util.log.showLogError
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase.AddNewContactsUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ContactListObserver @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val saveDataStoreItemUseCase: SaveDataStoreItemUseCase<Long>,
    private val addNewContactsUseCase: AddNewContactsUseCase,
    private val contentResolver: ContentResolver,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
    @ContactObserverHandler private val handler: Handler
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        coroutineScope.launch {
            val contactsLastTimestampDeferred = coroutineScope.async {
                readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first() ?: 0
            }
            val lastTimestamp: Long = contactsLastTimestampDeferred.await()

            val contactsListAsync = async { getContactsItems(lastTimestamp) }
            val contactNumbersAsync = async { getContactNumbers(lastTimestamp) }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            val contactsList = ArrayList<ContactEntity>()

            contacts.forEach { contactEntity ->
                contactNumbers[contactEntity.contactId]?.let { numbers ->
                    numbers.forEach {
                        contactsList.add(
                            ContactEntity(
                                contactId = contactEntity.contactId,
                                name = contactEntity.name,
                                phone = it
                            )
                        )
                    }
                }
            }
            launch {
                addNewContactsUseCase(contactsList)
            }
        }
    }
    fun unregister() {
        contentResolver.unregisterContentObserver(this)
    }
    fun register() {
        contentResolver.registerContentObserver(
            ContactsContract.DeletedContacts.CONTENT_URI,
            true,
            this
        )
    }

    private fun getContactNumbers(lastTimestamp: Long): HashMap<Long, ArrayList<String>> {
        val contactsNumberMap = HashMap<Long, ArrayList<String>>()

        showLog("num time", lastTimestamp.toString())
//        val selection =
//            "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} > $lastTimestamp"

        val selection = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?"
        val selectionArgs = arrayOf(lastTimestamp.toString())

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            selection,
            selectionArgs,
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
                    val number: String = contactsCursor.getString(numberIndex)

                    if (contactsNumberMap.containsKey(contactId)) {
                        contactsNumberMap[contactId]?.add(number)
//                        showLog("number ",number)
                    } else {
                        contactsNumberMap[contactId] = arrayListOf(number)
                    }
                } while (contactsCursor.moveToNext())
            }
        }
        return contactsNumberMap
    }

    private suspend fun getContactsItems(lastTimestamp: Long): List<ContactEntity> {
        val contactsList = ArrayList<ContactEntity>()

        showLog("name time", lastTimestamp.toString())
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        )

        val selection =
            "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} > $lastTimestamp"
        val sortOrder =
            "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        var newTimestamp: Long = 0

        cursor?.use { contactsCursor ->
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val timeIndex =
                contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)
//            val deletedContactIndex = contactsCursor.getColumnIndex(ContactsContract.DeletedContacts.CONTACT_ID)


            if (contactsCursor.moveToFirst()) {
                newTimestamp = contactsCursor.getLong(timeIndex)
                do {
                    val id = contactsCursor.getLong(idIndex)
                    val name = contactsCursor.getString(nameIndex)
//                    val deletedContactId = contactsCursor.getLong(deletedContactIndex)
//                    showLogError("del", "$deletedContactId")
                    showLog(msg =  "$name  $id")
                    if (name != null) {
                        contactsList.add(ContactEntity(id, name))
                    }
                } while (contactsCursor.moveToNext())
            }
        }
        coroutineScope {
            launch {
                saveDataStoreItemUseCase(
                    SaveDataStoreItemUseCase.Params.create(
                        PreferencesKeys.contactsLastTimestamp,
                        newTimestamp
                    )
                )
            }
        }
        return contactsList
    }
}