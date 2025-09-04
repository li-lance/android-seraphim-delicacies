package com.seraphim.yxsg.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CalendarPageDestination
import com.seraphim.yxsg.R
import com.seraphim.yxsg.ui.LocalDestinationsNavigator
import com.seraphim.yxsg.viewmodel.CalendarViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate

@Destination<RootGraph>(start = true)
@Composable
fun HomePage() {
    val navController = LocalDestinationsNavigator.current
    val viewModel: CalendarViewModel = koinViewModel()
    val taskFlow = remember { viewModel.getTodayTask() }
    val task by taskFlow.collectAsStateWithLifecycle()

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
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "notifications"
                )
            }
        })
        Text("今日堂食记录")
        Text(LocalDate.now().month.value.toString() + "月" + LocalDate.now().dayOfMonth + "日")
        Text(LocalDate.now().dayOfWeek.displayText())
        TodayView(
            task.first,
            task.second,
            viewModel,
            CalendarDay(LocalDate.now(), DayPosition.MonthDate)
        )
    }
}