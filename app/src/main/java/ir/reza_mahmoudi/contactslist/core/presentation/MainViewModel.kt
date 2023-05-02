package ir.reza_mahmoudi.contactslist.core.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer.ContactListObserver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
    private val contactListObserver: ContactListObserver,
) : ViewModel() {

    val contactsLastTimestamp = runBlocking {
        readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first()
    }

    override fun onCleared() {
        super.onCleared()
        stopObservingContacts()
    }

    fun startObservingContacts() {
        contactListObserver.register()
    }

    private fun stopObservingContacts() {
        contactListObserver.unregister()
    }
}