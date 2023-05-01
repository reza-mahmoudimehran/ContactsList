package ir.reza_mahmoudi.contactslist.feature_contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.core.util.log.showLog
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_contacts_list.GetContactsListUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_phone_contacts.GetPhoneContactUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val getPhoneContactUseCase: GetPhoneContactUseCase,
    private val getContactsListUseCase: GetContactsListUseCase
) : ViewModel() {

    val contactsLastTimestamp = runBlocking {
        readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first()
    }

    private val _contactsList = MutableStateFlow<List<ContactEntity>?>(null)
    val contactsList: StateFlow<List<ContactEntity>?>
        get() = _contactsList

    fun getPhoneContactsList() {
        viewModelScope.launch {
            getPhoneContactUseCase()
            getLocalContactsList()
        }
    }
    fun getLocalContactsList() {
        viewModelScope.launch {
            getContactsListUseCase(Unit).collectLatest {
                _contactsList.value = it
            }
        }
    }
}