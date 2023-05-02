package ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_contacts_list

import ir.reza_mahmoudi.contactslist.core.di.qualifiers.IoDispatcher
import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.FlowUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsListUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<ContactEntity>>(ioDispatcher) {
    override suspend fun execute(parameters: Unit): Flow<List<ContactEntity>> = contactsRepository.getContactsList()
}
