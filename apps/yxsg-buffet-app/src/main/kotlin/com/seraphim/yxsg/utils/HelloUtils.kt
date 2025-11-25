package com.seraphim.yxsg.utils

import java.time.LocalTime
import java.time.ZoneId

class HelloUtils {
    companion object {
        /**
         * 根据时间返回问候文案：
         * 11:00 前 -> 早上好
         * 14:00 前 -> 中午好
         * 17:00 前 -> 下午好
         * 20:00 前 -> 晚上好
         * 20:00 及以后 -> 晚安
         *
         * 可传入 now 以便测试，默认使用系统时区的当前时间。
         */
        @JvmStatic
        fun greeting(now: LocalTime = LocalTime.now(ZoneId.systemDefault())): String {
            val hour = now.hour
            return when {
                hour < 11 -> "早上好"
                hour < 14 -> "中午好"
                hour < 17 -> "下午好"
                hour < 20 -> "晚上好"
                else -> "晚安"
            }
        }
    }
}