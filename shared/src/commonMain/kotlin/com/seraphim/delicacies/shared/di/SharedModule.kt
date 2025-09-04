package com.seraphim.delicacies.shared.di

import com.seraphim.delicacies.shared.database.AppDatabase
import com.seraphim.delicacies.shared.network.ApiService
import com.seraphim.delicacies.shared.network.ApiServiceImpl
import com.seraphim.delicacies.shared.network.HttpClientFactory
import io.ktor.client.HttpClient
import org.koin.dsl.module

val sharedModule = module {
    single { get<AppDatabase>().taskDao() }
    single<HttpClient> { HttpClientFactory.create("") }
    single<ApiService> { ApiServiceImpl(get()) }
}