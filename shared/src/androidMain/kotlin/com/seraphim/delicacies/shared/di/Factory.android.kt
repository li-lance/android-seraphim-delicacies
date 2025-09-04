package com.seraphim.delicacies.shared.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.seraphim.delicacies.shared.database.AppDatabase
import com.seraphim.delicacies.shared.database.DB_FILE_NAME
import kotlinx.coroutines.Dispatchers

actual class Factory(private val context: Context) {
    actual fun createRoomDatabase(): AppDatabase {
        val dbFile = context.getDatabasePath(DB_FILE_NAME)
        return Room
            .databaseBuilder<AppDatabase>(
                context = context,
                name = dbFile.absolutePath,
            ).setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

}