package ir.reza_mahmoudi.contactslist.feature_contacts.domain.delete_contacts.usecase

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.LocalContactsRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteContactsUseCase @Inject constructor(
    private val localContactsRepository: LocalContactsRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<List<String>, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: List<String>) {
        localContactsRepository.deleteContactsByNumbers(parameters)
    }
}
