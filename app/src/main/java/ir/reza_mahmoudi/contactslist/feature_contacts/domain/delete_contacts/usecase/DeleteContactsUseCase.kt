package ir.reza_mahmoudi.contactslist.feature_contacts.domain.delete_contacts.usecase

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteContactsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<List<Long>, Unit>(
    ioDispatcher
) {
    override suspend fun execute(parameters: List<Long>) {
        contactsRepository.deleteContactsByIds(parameters)
    }
}
