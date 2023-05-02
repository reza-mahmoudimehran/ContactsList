package ir.reza_mahmoudi.contactslist.feature_contacts.domain.contact_observer.usecase

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.UseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ObserveContactsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
) : UseCase<Unit, Unit>() {
    override fun execute(parameters: Unit) {
        coroutineScope.launch {
            val contactsLastTimestampDeferred = async {
                dataStoreRepository.read(PreferencesKeys.contactsLastTimestamp).first() ?: 0
            }
            val lastTimestamp: Long = contactsLastTimestampDeferred.await()

            val savedContactsNumberListAsync = async {
                dataStoreRepository.read(PreferencesKeys.savedContactsNumberList).first()
                    ?: HashSet()
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
                contactsRepository.deleteAndInsertContacts(deleteList, contactsList)
            }
            launch(ioDispatcher) {
                dataStoreRepository.save(
                    PreferencesKeys.savedContactsNumberList,
                    addList
                )
            }
        }
    }


    private fun getContactsItems(lastTimestamp: Long): List<ContactEntity> {
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

    private data class ContactsPhoneNumberMap(
        val phoneNumberSets: Set<String>,
        val phoneNumberMap: HashMap<Long, ArrayList<String>>
    )
}
