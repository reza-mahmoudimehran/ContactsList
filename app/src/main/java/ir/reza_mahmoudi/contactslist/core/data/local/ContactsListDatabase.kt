package ir.reza_mahmoudi.contactslist.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.reza_mahmoudi.contactslist.core.util.constant.AppConstants.DATABASE_NAME

@Database(
    entities = [],
    version = 1 ,
    exportSchema = false
)
abstract class ContactsListDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: ContactsListDatabase? = null

        fun getDatabase(context: Context): ContactsListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsListDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}