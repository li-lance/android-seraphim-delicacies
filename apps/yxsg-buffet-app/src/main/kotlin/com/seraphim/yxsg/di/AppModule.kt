package com.seraphim.yxsg.di

import com.seraphim.delicacies.shared.di.Factory
import com.seraphim.yxsg.viewmodel.CalendarViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { Factory(androidApplication()).createRoomDatabase() }
    viewModelOf(::CalendarViewModel)
}