package ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity

data class ContactsPhoneNumberMap(
    val phoneNumberSets: Set<String>,
    val phoneNumberMap: HashMap<Long, ArrayList<String>>
)