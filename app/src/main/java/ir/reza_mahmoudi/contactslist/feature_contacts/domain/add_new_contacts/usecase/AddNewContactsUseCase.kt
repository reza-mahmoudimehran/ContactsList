package ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.LocalContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddNewContactsUseCase @Inject constructor(
    private val localContactsRepository: LocalContactsRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<List<ContactEntity>, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: List<ContactEntity>) {
        localContactsRepository.addNewContacts(parameters)
    }
}
