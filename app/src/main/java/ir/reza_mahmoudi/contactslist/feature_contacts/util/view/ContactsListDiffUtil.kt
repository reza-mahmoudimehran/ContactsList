package ir.reza_mahmoudi.contactslist.feature_contacts.util.view

import androidx.recyclerview.widget.DiffUtil
import ir.reza_mahmoudi.contactslist.feature_contacts.domain.common.entity.ContactEntity

class ContactsListDiffUtil(
    private val oldList: List<ContactEntity>,
    private val newList: List<ContactEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].phone == newList[newItemPosition].phone &&
                oldList[oldItemPosition].name == newList[newItemPosition].name
    }
}