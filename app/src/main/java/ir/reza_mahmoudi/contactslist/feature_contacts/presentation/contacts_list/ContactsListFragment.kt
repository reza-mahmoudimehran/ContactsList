package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.contacts_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.reza_mahmoudi.contactslist.R
import ir.reza_mahmoudi.contactslist.core.util.log.showLog
import ir.reza_mahmoudi.contactslist.databinding.FragmentContactsListBinding
import ir.reza_mahmoudi.contactslist.feature_contacts.presentation.ContactsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsListFragment : Fragment() {

    private val viewModel: ContactsViewModel by viewModels()

    private lateinit var binding: FragmentContactsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        collectData()
    }

    private fun setupViews() {}

    private fun collectData() {
        viewModel.getContactsList()
        lifecycleScope.launch {
            launch {
                viewModel.contactsList.collectLatest {
                    binding.txtContactsList.text = it.toString()
//                    showLog("contacts list", it.toString())
                }
            }
        }
    }
}