package ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase

import androidx.datastore.preferences.core.Preferences
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ReadDataStoreItemUseCase<T> @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Preferences.Key<T>, T?>(dispatcher) {
    override suspend fun execute(parameters: Preferences.Key<T>): Flow<T?> =
        dataStoreRepository.read(parameters)
}