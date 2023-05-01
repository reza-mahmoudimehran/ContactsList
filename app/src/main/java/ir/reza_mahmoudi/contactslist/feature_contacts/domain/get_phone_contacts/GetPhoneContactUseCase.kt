package ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_phone_contacts

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.SaveDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.util.log.showLog
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase.AddNewContactsUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPhoneContactUseCase @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val saveDataStoreItemUseCase: SaveDataStoreItemUseCase<Long>,
    private val addNewContactsUseCase: AddNewContactsUseCase,
    private val contentResolver: ContentResolver,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
) {

    operator fun invoke() {
        coroutineScope.launch {
            val contactsLastTimestampDeferred = coroutineScope.async {
                readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first()
            }
            val lastTimestamp: Long? = contactsLastTimestampDeferred.await()

            if (lastTimestamp==null){
                val contactsListAsync = async { getContactsItems() }
                val contactNumbersAsync = async { getContactNumbers() }

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
    }

    private fun getContactNumbers(): HashMap<Long, ArrayList<String>> {
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
                    val number: String = contactsCursor.getString(numberIndex)

                    if (contactsNumberMap.containsKey(contactId)) {
                        contactsNumberMap[contactId]?.add(number)
                    } else {
                        contactsNumberMap[contactId] = arrayListOf(number)
                    }
                } while (contactsCursor.moveToNext())
            }
        }
        return contactsNumberMap
    }

    private suspend fun getContactsItems(): List<ContactEntity> {
        val contactsList = ArrayList<ContactEntity>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        )
        val sortOrder =
            "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
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