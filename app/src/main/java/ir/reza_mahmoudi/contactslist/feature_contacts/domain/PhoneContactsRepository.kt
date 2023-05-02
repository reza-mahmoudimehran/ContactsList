package ir.reza_mahmoudi.contactslist.feature_contacts.domain

interface PhoneContactsRepository {
    fun fetchPhoneContacts()
    fun handleContactsChanges()
}