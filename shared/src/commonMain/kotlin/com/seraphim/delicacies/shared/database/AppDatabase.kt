package com.seraphim.delicacies.shared.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.seraphim.delicacies.shared.database.converter.ListStringConverter
import com.seraphim.delicacies.shared.database.dao.TaskDao
import com.seraphim.delicacies.shared.database.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(value = [ListStringConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal const val DB_FILE_NAME = "seraphim-delicacies.db"