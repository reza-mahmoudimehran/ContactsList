package ir.reza_mahmoudi.contactslist.core.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.reza_mahmoudi.contactslist.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val CONTACTS_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkContactsPermission()) {
            viewModel.startObservingContacts()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE)
        }

    }

    private fun checkContactsPermission(): Boolean {
        val permission = Manifest.permission.READ_CONTACTS
        val granted = PackageManager.PERMISSION_GRANTED

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionStatus = ContextCompat.checkSelfPermission(this, permission)
            permissionStatus == granted
        } else {
            true
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.startObservingContacts()
        } else {
            Toast.makeText(this, "We need contacts permission",Toast.LENGTH_LONG).show()
        }
    }
}