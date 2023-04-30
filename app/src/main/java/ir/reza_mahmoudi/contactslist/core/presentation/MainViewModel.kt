package ir.reza_mahmoudi.contactslist.core.presentation

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer.ContactListObserver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val contactListObserver: ContactListObserver,
    private val contentResolver: ContentResolver,
    ) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        stopObservingContacts()
    }

    fun startObservingContacts() {
        viewModelScope.launch {
//            val contactsLastTimestamp = readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first()
//            contactsLastTimestamp?.let {
                contentResolver.registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contactListObserver
                )
//            }
        }
    }

    private fun stopObservingContacts() {
        contentResolver.unregisterContentObserver(contactListObserver)
    }
}