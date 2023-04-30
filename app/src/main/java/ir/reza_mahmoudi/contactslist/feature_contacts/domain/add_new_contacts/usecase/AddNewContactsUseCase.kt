package ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.SuspendUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddNewContactsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<List<ContactEntity>, Unit>(
    ioDispatcher
) {
    override suspend fun execute(parameters: List<ContactEntity>) {
        contactsRepository.addNewContacts(parameters)
    }
}
