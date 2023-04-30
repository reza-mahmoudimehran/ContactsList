package ir.reza_mahmoudi.contactslist.core.domain.data_store.preferences

import androidx.datastore.preferences.core.preferencesKey

object PreferencesKeys {
    val contactsLastTimestamp = preferencesKey<Long>("contacts_last_timestamp")
}