package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.cantacts_list_observer

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.provider.ContactsContract
import ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers.ContactObserverHandler
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.contact_observer.usecase.ObserveContactsUseCase
import javax.inject.Inject

class ContactListObserver @Inject constructor(
    private val observeContactsUseCase: ObserveContactsUseCase,
    private val contentResolver: ContentResolver,
    @ContactObserverHandler private val handler: Handler
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        observeContactsUseCase(Unit)
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
}