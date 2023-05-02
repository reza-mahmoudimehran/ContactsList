package ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences

import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.preferencesSetKey

object PreferencesKeys {
    val contactsLastTimestamp = preferencesKey<Long>("contacts_last_timestamp")
    val savedContactsNumberList = preferencesSetKey<String>("saved_contacts_number_list")
}