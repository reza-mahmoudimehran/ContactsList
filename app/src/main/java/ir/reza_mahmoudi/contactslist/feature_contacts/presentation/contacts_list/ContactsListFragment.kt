package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.contacts_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.reza_mahmoudi.contactslist.core.presentation.MainActivity
import ir.reza_mahmoudi.contactslist.databinding.FragmentContactsListBinding
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.ContactsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsListFragment : Fragment() {

    private val viewModel: ContactsViewModel by viewModels()
    private lateinit var mActivity: MainActivity
    private lateinit var binding: FragmentContactsListBinding
    private lateinit var contactsAdapter: ContactsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        fetchContacts()
        collectData()
    }

    private fun fetchContacts() {
        if (viewModel.contactsLastTimestamp == null) {
            checkContactsPermission()
        } else {
            viewModel.getLocalContactsList()
        }
    }

    private fun fetchContactsFirstTime() {
        viewModel.getPhoneContactsList()
        mActivity.observingContactsChanges()
    }

    private fun setupViews() {
        contactsAdapter = ContactsListAdapter { contact ->
            findNavController().navigate(
                ContactsListFragmentDirections.actionFrgContactsListToFrgContactDetails(
                    contact
                )
            )
        }

        binding.rcvContactsList.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = contactsAdapter
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            launch {
                viewModel.contactsList.collectLatest {
                    if (!it.isNullOrEmpty()) {
                        contactsAdapter.setData(it)
                    }
                }
            }
        }
    }

    private fun checkContactsPermission() {
        val permission = Manifest.permission.READ_CONTACTS

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchContactsFirstTime()
        } else {
            requestPermission(permission)
        }
    }

    private fun requestPermission(permission: String) {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                fetchContactsFirstTime()
            } else {
                Toast.makeText(requireActivity(), "We need contacts permission", Toast.LENGTH_LONG)
                    .show()
            }
        }.launch(permission)
    }
}