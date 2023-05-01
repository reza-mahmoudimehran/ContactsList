package ir.reza_mahmoudi.contactslist.core.presentation

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences.PreferencesKeys
import ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase.ReadDataStoreItemUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer.ContactListObserver
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer.DeletedContactsObserver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readDataStoreItemUseCase: ReadDataStoreItemUseCase<Long>,
//    private val contactListObserver: ContactListObserver,
    private val deletedContactListObserver: DeletedContactsObserver,
    ) : ViewModel() {

    val contactsLastTimestamp = runBlocking {
        readDataStoreItemUseCase(PreferencesKeys.contactsLastTimestamp).first()
    }

    override fun onCleared() {
        super.onCleared()
        stopObservingContacts()
    }

    fun startObservingContacts() {
        deletedContactListObserver.register()
    }

    private fun stopObservingContacts() {
        deletedContactListObserver.unregister()
    }
}