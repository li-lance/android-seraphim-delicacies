package com.seraphim.yxsg.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.seraphim.yxsg.ui.LocalDestinationsNavigator
import com.seraphim.yxsg.ui.theme.Black
import com.seraphim.yxsg.ui.theme.OnBackgroundDark
import com.seraphim.yxsg.ui.theme.Typography
import com.seraphim.yxsg.ui.theme.White
import com.seraphim.yxsg.viewmodel.CalendarViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Destination<RootGraph>()
@Composable
fun CalendarPage() {
    val navController = LocalDestinationsNavigator.current
    val viewModel: CalendarViewModel = koinViewModel()
    val selectedDay by viewModel.selectedDateFlow.collectAsStateWithLifecycle()
    val isLunchChecked by viewModel.isLunchChecked.collectAsStateWithLifecycle()
    val isDinnerChecked by viewModel.isDinnerChecked.collectAsStateWithLifecycle()
    val daysOfWeek = remember { daysOfWeek() }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(title = { Text(text = "食光记录") }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "calendar"
                )
            }
        })
        Text(
            "${YearMonth.now()}",
            modifier = Modifier.padding(16.dp),
//            color = Black,
            style = Typography.headlineLarge
        )
        HorizontalCalendar(modifier = Modifier.wrapContentWidth(), dayContent = { day ->
            Day(viewModel = viewModel, day = day, isSelected = selectedDay == day) {
                viewModel.updateSelectedDate(it)
            }
        }, monthHeader = {
            MonthHeader(
                modifier = Modifier.padding(vertical = 8.dp),
                daysOfWeek = daysOfWeek,
            )
        })
        Text(
            selectedDay.date.month.value.toString() + "月" + selectedDay.date.dayOfMonth.toString() + "日",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = Typography.titleLarge,
        )
        TodayView(isLunchChecked, isDinnerChecked, viewModel, selectedDay)
    }
}

@Composable
private fun Day(
    viewModel: CalendarViewModel,
    day: CalendarDay,
    isSelected: Boolean = false,
    colors: List<Color> = emptyList(),
    onClick: (CalendarDay) -> Unit = {},
) {
    // 将日期转为yyyy-MM-dd (CalendarDay.date 是 LocalDate, toString 即 ISO-8601 yyyy-MM-dd)
    val dayString = day.date.toString()
    val today = remember { LocalDate.now() }
    // 收集当天午餐+晚餐次数 (0,1,2)
    val taskFlow = remember(dayString) { viewModel.getCountByDay(dayString) }
    val mealCount by taskFlow.collectAsStateWithLifecycle()

    // 根据次数构造颜色条（示例：一次=绿色；两次=绿+蓝）你可按需要调整
    val barColors = when (mealCount) {
        0 -> emptyList()
        1 -> listOf(Color(0xFF4CAF50))
        else -> listOf(Color(0xFF4CAF50), Color(0xFF2196F3))
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(color = if (isSelected) Color.Red else White)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && !day.date.isAfter(today),
                onClick = { onClick(day) },
            ),
    ) {
        val textColor = when (day.position) {
            DayPosition.MonthDate -> Black
            DayPosition.InDate, DayPosition.OutDate -> OnBackgroundDark
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 3.dp, end = 4.dp),
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 12.sp,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            for (color in barColors) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(color),
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek> = emptyList(),
) {
    Row(modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Black,
                text = dayOfWeek.displayText(uppercase = true),
                fontWeight = FontWeight.Light,
            )
        }
    }
}

@Composable
fun TodayView(
    isLunchChecked: Boolean,
    isDinnerChecked: Boolean,
    viewModel: CalendarViewModel,
    selectedDay: CalendarDay
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("午餐")
        Checkbox(
            checked = isLunchChecked,
            onCheckedChange = {
                viewModel.updateLunch(
                    selectedDay.date.toString(),
                    isLunch = it,
                )
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("晚餐")
        Checkbox(
            checked = isDinnerChecked,
            onCheckedChange = {
                viewModel.updateDinner(
                    selectedDay.date.toString(),
                    isDinner = it,
                )
            }
        )
    }
}


fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

fun DayOfWeek.displayText(uppercase: Boolean = false, narrow: Boolean = false): String {
    val style = if (narrow) TextStyle.NARROW else TextStyle.SHORT
    return getDisplayName(style, Locale.CHINA).let { value ->
        if (uppercase) value.uppercase(Locale.CHINA) else value
    }
}