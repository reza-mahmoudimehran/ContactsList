package ir.reza_mahmoudi.contactslist.feature_contacts.data.repository

import android.content.ContentResolver
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.feature_contacts.data.local.ContactsDao
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverCoroutineScope
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.PhoneContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.util.contacts.getAllContactsNames
import ir.reza_mahmoudi.contactslist.feature_contacts.util.contacts.getChangedContactsNames
import ir.reza_mahmoudi.contactslist.feature_contacts.util.contacts.getContactNumbers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhoneContactsRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    private val dataStoreRepository: DataStoreRepository,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ContactObserverCoroutineScope private val coroutineScope: CoroutineScope,
) : PhoneContactsRepository {

    override fun handleContactsChanges() {
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

            val contactsName = getChangedContactsNames(
                dataStoreRepository = dataStoreRepository,
                contentResolver = contentResolver,
                ioDispatcher = ioDispatcher,
                coroutineScope = coroutineScope,
                lastTimestamp = lastTimestamp
            )
            val contactNumbers = getContactNumbers(contentResolver = contentResolver)

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
                contactsDao.deleteAndInsertContacts(deleteList, contactsList)
            }
            launch(ioDispatcher) {
                dataStoreRepository.save(
                    PreferencesKeys.savedContactsNumberList,
                    addList
                )
            }
        }
    }

    override fun fetchPhoneContacts() {
        coroutineScope.launch {
            val contactsLastTimestampDeferred = coroutineScope.async {
                dataStoreRepository.read(PreferencesKeys.contactsLastTimestamp).first()
            }
            val lastTimestamp: Long? = contactsLastTimestampDeferred.await()

            if (lastTimestamp == null) {
                val contacts = getAllContactsNames(
                    dataStoreRepository = dataStoreRepository,
                    contentResolver = contentResolver,
                    ioDispatcher = ioDispatcher,
                    coroutineScope = coroutineScope,
                )
                val contactNumbers = getContactNumbers(contentResolver = contentResolver)

                val contactsList = ArrayList<ContactEntity>()

                contacts.forEach { contactEntity ->
                    contactNumbers.phoneNumberMap[contactEntity.contactId]?.let { numbers ->
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

                launch(ioDispatcher) {
                    contactsDao.addNewContacts(contactsList)
                }
                launch(ioDispatcher) {
                    dataStoreRepository.save(
                        PreferencesKeys.savedContactsNumberList,
                        contactNumbers.phoneNumberSets
                    )
                }
            }
        }
    }

}