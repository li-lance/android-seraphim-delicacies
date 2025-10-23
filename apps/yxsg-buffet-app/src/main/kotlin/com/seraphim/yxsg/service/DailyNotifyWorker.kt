package com.seraphim.yxsg.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.seraphim.yxsg.utils.NotificationUtils
import java.util.Calendar

class DailyNotifyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        NotificationUtils.showNotification(
            applicationContext,
            "每日提醒",
            "记得查看今日餐饮安排！"
        )
        return Result.success()
    }
}

fun calculateInitialDelay(): Long {
    val currentTime = Calendar.getInstance()
    val targetTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 20)  // 设为每天9点
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    if (currentTime.after(targetTime)) {
        targetTime.add(Calendar.DAY_OF_YEAR, 1)
    }
    return targetTime.timeInMillis - currentTime.timeInMillis
}