package com.seraphim.yxsg

import android.app.Application
import com.seraphim.delicacies.domain.di.domainModule
import com.seraphim.delicacies.shared.di.sharedModule
import com.seraphim.utils.initLogger
import com.seraphim.utils.initMMKV
import com.seraphim.yxsg.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class YssgApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initMMKV(this)
        initLogger()
        startKoin {
            androidLogger()
            androidContext(this@YssgApplication)
            modules(appModule + sharedModule + domainModule)
        }
    }
}