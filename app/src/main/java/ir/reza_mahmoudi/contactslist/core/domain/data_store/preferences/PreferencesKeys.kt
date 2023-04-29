package ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences

import androidx.datastore.preferences.core.preferencesKey

object PreferencesKeys {
    val listenToContactsChange = preferencesKey<Boolean>("listen_to_contacts_change")
}