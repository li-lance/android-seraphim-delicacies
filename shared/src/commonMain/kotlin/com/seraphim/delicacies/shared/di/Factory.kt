package com.seraphim.delicacies.shared.di

import com.seraphim.delicacies.shared.database.AppDatabase


expect class Factory {
    fun createRoomDatabase(): AppDatabase
}