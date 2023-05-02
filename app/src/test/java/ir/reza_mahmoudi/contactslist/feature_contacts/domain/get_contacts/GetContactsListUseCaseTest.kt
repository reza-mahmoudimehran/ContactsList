package ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_contacts

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.ContactsRepository
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.get_contacts_list.GetContactsListUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@SmallTest
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetContactsListUseCaseTest {

    private val testDispatcher: TestDispatcher by lazy { StandardTestDispatcher() }

    private val coroutineScope: CoroutineScope = CoroutineScope(testDispatcher)

    @Mock
    private lateinit var mockContactsRepository: ContactsRepository

    private lateinit var getContactsListUseCase: GetContactsListUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getContactsListUseCase = GetContactsListUseCase(mockContactsRepository, testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `execute should return flow of contact list from repository`() {
        coroutineScope.launch {
            runTest {
                val contactList = listOf(ContactEntity(1L, "Reza"), ContactEntity(2L, "Ali"))

                val contactFlow = flow { emit(contactList) }

                `when`(mockContactsRepository.getContactsList()).thenReturn(contactFlow)

                val resultFlow = getContactsListUseCase(Unit)

                val result = resultFlow.toList()

                verify(mockContactsRepository).getContactsList()

                assertThat(result).isEqualTo(contactList)
            }
        }
    }
}