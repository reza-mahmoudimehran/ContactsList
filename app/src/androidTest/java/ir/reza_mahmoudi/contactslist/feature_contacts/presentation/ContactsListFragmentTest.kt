package ir.reza_mahmoudi.contactslist.feature_contacts.presentation

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import ir.reza_mahmoudi.contactslist.core.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.*
import ir.reza_mahmoudi.contactslist.R
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.contacts_list.ContactsListAdapter

@HiltAndroidTest
@ExperimentalCoroutinesApi
class ContactsListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mActivityRule = ActivityScenarioRule(MainActivity::class.java)

    private val testDispatcher: TestDispatcher by lazy { StandardTestDispatcher() }


    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        grantContactsPermission()
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }


    @Test
    fun testContactsPermission() {
        runTest {
            mActivityRule.scenario.onActivity { activity ->
                val permissionStatus =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                assertThat(permissionStatus).isEqualTo(PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    @Test
    fun selectListItem_ShouldVisibleContactDetails() {
            onView(withId(R.id.rcvContactsList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<ContactsListAdapter.ViewHolder>(0, click())
                )

            onView(withId(R.id.txtAvatar)).check(matches(isDisplayed()))
            onView(withId(R.id.txtName)).check(matches(isDisplayed()))
            onView(withId(R.id.txtPhoneNumber)).check(matches(isDisplayed()))
    }

    private fun grantContactsPermission() {
        val permission = Manifest.permission.READ_CONTACTS
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm grant ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} $permission")
    }
}