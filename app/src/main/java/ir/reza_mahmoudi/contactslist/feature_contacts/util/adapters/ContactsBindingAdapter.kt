package ir.reza_mahmoudi.contactslist.feature_contacts.util.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import ir.reza_mahmoudi.contactslist.core.util.constant.ColorsConstants.AVATAR_COLORS
import kotlin.math.abs

class ContactsBindingAdapter {
    companion object {

        @BindingAdapter(value = ["phoneNumber", "name"])
        @JvmStatic
        fun TextView.setAvatarColor(phoneNumber: String, name: String) {
            text = name.take(2)
            val backgroundColor = AVATAR_COLORS[abs(phoneNumber.hashCode()) % AVATAR_COLORS.size]
            background.setTint(backgroundColor)
        }
    }
}