package com.seraphim.delicacies.domain.di

import com.seraphim.delicacies.domain.MealTaskRepository
import org.koin.dsl.module

val domainModule = module {
    single { MealTaskRepository(get()) }
}