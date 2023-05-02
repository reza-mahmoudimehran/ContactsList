package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.contact_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import ir.reza_mahmoudi.contactslist.databinding.FragmentContactDetailsBinding

class ContactDetailsFragment : Fragment() {

    private val args: ContactDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentContactDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactDetailsBinding.inflate(layoutInflater)
        binding.contact = args.contact
        return binding.root
    }
}