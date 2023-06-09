package ir.reza_mahmoudi.contactslist.feature_contacts.presentation.contacts_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.reza_mahmoudi.contactslist.databinding.ItemContactBinding
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity
import ir.reza_mahmoudi.contactslist.feature_contacts.util.view.ContactsListDiffUtil

class ContactsListAdapter(
    private val onItemClick: (ContactEntity) -> Unit
) : RecyclerView.Adapter<ContactsListAdapter.ViewHolder>() {

    private var contacts = emptyList<ContactEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
        holder.itemView.setOnClickListener { onItemClick(contact) }
    }

    override fun getItemCount(): Int = contacts.size

    fun setData(newMoviesList: List<ContactEntity>) {
        val contactsDiffUtil =
            ContactsListDiffUtil(contacts, newMoviesList)
        val diffUtilResult = DiffUtil.calculateDiff(contactsDiffUtil)
        contacts = newMoviesList
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: ContactEntity) {
            binding.contact = contact
        }
    }
}