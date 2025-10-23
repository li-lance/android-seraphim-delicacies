package com.seraphim.yxsg.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.seraphim.yxsg.R

object NotificationUtils {
    fun showNotification(context: Context, title: String, content: String) {
        val channelId = "daily_notify_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 创建通知渠道
        val channel = NotificationChannel(
            channelId,
            "每日通知",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 替换为你的图标
            .build()

        manager.notify(1001, notification)
    }
}