package ir.reza_mahmoudi.contactslist.core.domain.data_store.usecase

import androidx.datastore.preferences.core.Preferences
import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.core.domain.data_store.DataStoreRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


class SaveDataStoreItemUseCase<T> @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<SaveDataStoreItemUseCase.Params<T>, Unit>(dispatcher) {
    override suspend fun execute(parameters: Params<T>) =
        dataStoreRepository.save(parameters.key, parameters.value)

    class Params<T> private constructor(
        val key: Preferences.Key<T>,
        val value: T
    ) {
        companion object {
            @JvmStatic
            fun <T> create(
                key: Preferences.Key<T>,
                value: T
            ): Params<T> {
                return Params(key, value)
            }
        }
    }
}