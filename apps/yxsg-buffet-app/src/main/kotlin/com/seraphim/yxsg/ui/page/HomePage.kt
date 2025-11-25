package com.seraphim.yxsg.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CalendarPageDestination
import com.seraphim.yxsg.R
import com.seraphim.yxsg.ui.LocalDestinationsNavigator
import com.seraphim.yxsg.ui.theme.Typography
import com.seraphim.yxsg.utils.HelloUtils
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
    val task by viewModel.todayTask.collectAsStateWithLifecycle()
    val imageUrl by viewModel.imageUrl.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.fetchImage()
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.getTodayTask()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 24.dp)
                .background(
                    Color.Black,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                HelloUtils.greeting() + "，" + "Lance",
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                style = Typography.titleLarge.copy(fontWeight = FontWeight(600))
            )
            Text(
                "今天是个好日子，记得按时去堂食哦~",
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
            )
        }
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = LocalDate.now().month.value.toString() + "月" + LocalDate.now().dayOfMonth + "日",
                style = Typography.bodyMedium.copy(fontSize = 20.sp, fontWeight = FontWeight(500))
            )
            Text(
                text = "(" + LocalDate.now().dayOfWeek.displayText() + ")",
                style = Typography.bodyMedium.copy(fontSize = 20.sp, fontWeight = FontWeight(500))
            )
        }
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
//                .diskCachePolicy(CachePolicy.ENABLED) // 启用磁盘缓存
//                .networkCachePolicy(CachePolicy.ENABLED) // 启用网络缓存
//                .crossfade(true) // 平滑过渡
//                .build(),
//            contentDescription = "header image",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(160.dp)
//
//        )
        Box(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SegmentedRingChart(
                        value = total.toFloat(),
                        max = 20,
                        segmentThickness = 20.dp,
                        gapAngle = 4f,
                        activeColor = Color(0xFF3F51B5),
                        inactiveColor = Color(0xFFFFFFFF),
                        roundCaps = true,
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(1f)
                            .align(Alignment.Center)
                            .padding(16.dp),
                        valueFormatter = { v, m -> "${"%.1f".format(v)}/$m" }
                    )
                }
            }
        }

        TodayView(
            task.first,
            task.second,
            total,
            viewModel,
            CalendarDay(LocalDate.now(), DayPosition.MonthDate)
        )
    }
}