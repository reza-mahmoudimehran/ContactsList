package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.SaveDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase.AddNewContactsUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.delete_contacts.usecase.DeleteContactsUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ContactListObserver @Inject constructor(
    private val readLastTimestampUseCase: ReadDataStoreItemUseCase<Long>,
    private val saveLastTimestampItemUseCase: SaveDataStoreItemUseCase<Long>,
    private val readContactIdsUseCase: ReadDataStoreItemUseCase<Set<String>>,
    private val saveContactIdsUseCase: SaveDataStoreItemUseCase<Set<String>>,
    private val addNewContactsUseCase: AddNewContactsUseCase,
    private val deleteContactsUseCase: DeleteContactsUseCase,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
    @ContactObserverHandler private val handler: Handler
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        coroutineScope.launch {
            val contactsLastTimestampDeferred = coroutineScope.async {
                readLastTimestampUseCase(PreferencesKeys.contactsLastTimestamp).first() ?: 0
            }
            val lastTimestamp: Long = contactsLastTimestampDeferred.await()

            val savedContactsNumberListAsync = coroutineScope.async {
                readContactIdsUseCase(PreferencesKeys.savedContactsNumberList).first()
                    ?: setOf()
            }
            val savedContactsNumberList: MutableSet<String> =
                savedContactsNumberListAsync.await().toMutableSet()

            val contactsName = getContactsItems(lastTimestamp)
            val contactNumbers = getContactNumbers()

            val contactsList = ArrayList<ContactEntity>()

            contactsName.forEach { contactEntity ->
                contactNumbers.phoneNumberMap[contactEntity.contactId]?.let { numbers ->
                    numbers.forEach {
                        contactsList.add(
                            ContactEntity(
                                contactId = contactEntity.contactId,
                                name = contactEntity.name,
                                phone = it
                            )
                        )
                        if (!savedContactsNumberList.contains(it)) {
                            savedContactsNumberList.add(it)
                        }
                    }
                }
            }

            val deleteList = ArrayList<String>()
            val addList = HashSet<String>()

            savedContactsNumberList.forEach {
                if (contactNumbers.phoneNumberSets.contains(it)) {
                    addList.add(it)
                } else {
                    deleteList.add(it)
                }
            }
            launch(ioDispatcher) {
                addNewContactsUseCase(contactsList)
            }
            launch(ioDispatcher) {
                deleteContactsUseCase(deleteList)
            }
            launch(ioDispatcher) {
                saveContactIdsUseCase(
                    SaveDataStoreItemUseCase.Params.create(
                        PreferencesKeys.savedContactsNumberList,
                        addList
                    )
                )
            }
        }
    }

    private suspend fun getContactsItems(lastTimestamp: Long): List<ContactEntity> {
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
        coroutineScope {
            launch(ioDispatcher) {
                saveLastTimestampItemUseCase(
                    SaveDataStoreItemUseCase.Params.create(
                        PreferencesKeys.contactsLastTimestamp,
                        newTimestamp
                    )
                )
            }
        }
        return contactsList
    }

    private fun getContactNumbers(): ContactsPhoneNumberMap {
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

    private data class ContactsPhoneNumberMap(
        val phoneNumberSets: Set<String>,
        val phoneNumberMap: HashMap<Long, ArrayList<String>>
    )
}