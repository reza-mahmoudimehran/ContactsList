package ir.reza_mahmoudi.contactslist.core.domain.data_store

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun <T> save(key: Preferences.Key<T>, value: T)
    fun <T> read(key: Preferences.Key<T>): Flow<T?>
    suspend fun <T> remove(key: Preferences.Key<T>)
}