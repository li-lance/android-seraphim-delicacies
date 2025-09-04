package com.seraphim.delicacies.shared.di

import com.seraphim.delicacies.shared.database.AppDatabase
import org.koin.dsl.module

val sharedModule = module {
    single { get<AppDatabase>().taskDao() }
}