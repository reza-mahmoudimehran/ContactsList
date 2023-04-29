package ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase

import androidx.datastore.preferences.core.Preferences
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


class RemoveDataStoreItemUseCase <T> @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<Preferences.Key<T>, Unit>(dispatcher) {
    override suspend fun execute(parameters: Preferences.Key<T>) =
        dataStoreRepository.remove(parameters)
}