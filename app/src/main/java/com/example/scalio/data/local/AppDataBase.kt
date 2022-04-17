package com.example.scalio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scalio.R
import com.example.scalio.data.local.dao.RemoteKeysDao
import com.example.scalio.data.local.dao.UsersDao
import com.example.scalio.data.model.RemoteKeys
import com.example.scalio.data.model.User

@Database(
    entities = [
        User::class,
        RemoteKeys::class
    ],
    version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {

            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    context.getString(R.string.app_database)
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
