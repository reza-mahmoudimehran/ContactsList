package ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_contacts

import androidx.test.filters.SmallTest
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_new_contacts.usecase.AddNewContactsUseCase
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@SmallTest
class AddNewContactsUseCaseTest {

    private val testDispatcher: TestDispatcher by lazy { StandardTestDispatcher() }

    private val coroutineScope: CoroutineScope = CoroutineScope(testDispatcher)

    @Mock
    private lateinit var mockContactsRepository: ContactsRepository

    private lateinit var addNewContactsUseCase: AddNewContactsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addNewContactsUseCase = AddNewContactsUseCase(mockContactsRepository, testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should call addNewContacts on ContactsRepository`() {
        coroutineScope.launch {
            runTest {
                val contactsList = listOf(ContactEntity(1L, "Reza"), ContactEntity(2L, "Ali"))

                addNewContactsUseCase(contactsList)

                verify(mockContactsRepository).addNewContacts(contactsList)
            }
        }
    }
}