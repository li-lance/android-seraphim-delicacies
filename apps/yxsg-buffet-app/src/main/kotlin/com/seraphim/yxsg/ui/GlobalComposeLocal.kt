package com.seraphim.yxsg.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
/**
 * 全局路由
 * */
val LocalDestinationsNavigator = staticCompositionLocalOf<DestinationsNavigator> {
    error("DestinationsNavigator Unavailable")
}