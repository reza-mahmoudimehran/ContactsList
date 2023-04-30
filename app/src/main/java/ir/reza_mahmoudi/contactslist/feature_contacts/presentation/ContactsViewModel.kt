package ir.reza_mahmoudi.contactslist.feature_contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_contacts_list.GetContactsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase
) : ViewModel() {

    private val _contactsList = MutableStateFlow<List<ContactEntity>?>(null)
    val contactsList: StateFlow<List<ContactEntity>?>
        get() = _contactsList

    fun getContactsList() {
        viewModelScope.launch {
            getContactsListUseCase(Unit).collectLatest {
                _contactsList.value = it
            }
        }
    }
}