package com.seraphim.yxsg.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CalendarPageDestination
import com.seraphim.yxsg.R
import com.seraphim.yxsg.ui.LocalDestinationsNavigator
import com.seraphim.yxsg.ui.theme.Typography
import com.seraphim.yxsg.viewmodel.CalendarViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.time.YearMonth

@Destination<RootGraph>(start = true)
@Composable
fun HomePage() {
    val navController = LocalDestinationsNavigator.current
    val viewModel: CalendarViewModel = koinViewModel()
    val totalFlow = remember { viewModel.getMonthlyMealTotal(YearMonth.now().toString()) }
    val total by totalFlow.collectAsStateWithLifecycle()
    val taskFlow = remember { viewModel.getTodayTask() }
    val task by taskFlow.collectAsStateWithLifecycle()
    val imageUrl by viewModel.imageUrl.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.fetchImage()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.app_name)) }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate(CalendarPageDestination())
            }) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "calendar")
            }
        }, actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications, contentDescription = "notifications"
                )
            }
        })
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                .diskCachePolicy(CachePolicy.ENABLED) // 启用磁盘缓存
                .networkCachePolicy(CachePolicy.ENABLED) // 启用网络缓存
                .crossfade(true) // 平滑过渡
                .build(),
            contentDescription = "header image",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)

        )
        Text("今日堂食记录", style = Typography.titleLarge)
        Text(
            LocalDate.now().month.value.toString() + "月" + LocalDate.now().dayOfMonth + "日",
            style = Typography.bodyMedium
        )
        Text(LocalDate.now().dayOfWeek.displayText())
        TodayView(
            task.first, task.second, viewModel, CalendarDay(LocalDate.now(), DayPosition.MonthDate)
        )

        Text("本月堂食统计(${total})")
    }
}