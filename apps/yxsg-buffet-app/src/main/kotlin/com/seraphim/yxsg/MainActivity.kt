package com.seraphim.yxsg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.seraphim.yxsg.ui.page.MainPage
import com.seraphim.yxsg.ui.theme.SeraphimDelicaciesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SeraphimDelicaciesTheme {
                MainPage(navController = navController)
            }
        }
    }
}