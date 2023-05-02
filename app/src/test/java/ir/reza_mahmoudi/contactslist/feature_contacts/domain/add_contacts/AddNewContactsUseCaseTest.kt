package ir.reza_mahmoudi.contactslist.feature_contacts.domain.add_contacts

import androidx.test.filters.SmallTest
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.LocalContactsRepository
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

@SmallTest
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddNewContactsUseCaseTest {

    private val testDispatcher: TestDispatcher by lazy { StandardTestDispatcher() }

    private val coroutineScope: CoroutineScope = CoroutineScope(testDispatcher)

    @Mock
    private lateinit var mockLocalContactsRepository: LocalContactsRepository

    private lateinit var addNewContactsUseCase: AddNewContactsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addNewContactsUseCase = AddNewContactsUseCase(mockLocalContactsRepository, testDispatcher)
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

                verify(mockLocalContactsRepository).addNewContacts(contactsList)
            }
        }
    }
}