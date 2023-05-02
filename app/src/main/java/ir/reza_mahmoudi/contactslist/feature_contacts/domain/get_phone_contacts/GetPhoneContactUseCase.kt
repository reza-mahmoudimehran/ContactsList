package ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_phone_contacts

import ir.reza_mahmoudi.contactslist.core.domain.common.usecase.UseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.PhoneContactsRepository
import javax.inject.Inject

class GetPhoneContactUseCase @Inject constructor(
    private val phoneContactsRepository: PhoneContactsRepository
) : UseCase<Unit, Unit>() {
    override fun execute(parameters: Unit) = phoneContactsRepository.fetchPhoneContacts()
}