package ir.reza_mahmoudi.contactslist.feature_contacts.di.qualifiers

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ContactObserverCoroutineScope

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ContactObserverHandler