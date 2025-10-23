package com.seraphim.yxsg

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.seraphim.delicacies.domain.di.domainModule
import com.seraphim.delicacies.shared.di.sharedModule
import com.seraphim.utils.initLogger
import com.seraphim.utils.initMMKV
import com.seraphim.yxsg.di.appModule
import com.seraphim.yxsg.service.DailyNotifyWorker
import com.seraphim.yxsg.service.calculateInitialDelay
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

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
        notificationWorkManager()
    }

    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotifyWorker>(
        1, TimeUnit.DAYS
    )
        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS) // 可选：定点触发
        .build()

    private fun notificationWorkManager() {

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_notify",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}