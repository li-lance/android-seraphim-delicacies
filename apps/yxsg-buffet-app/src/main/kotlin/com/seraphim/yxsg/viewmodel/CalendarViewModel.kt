package com.seraphim.yxsg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.seraphim.delicacies.domain.MealTaskRepository
import com.seraphim.delicacies.shared.database.entity.TaskEntity
import com.seraphim.utils.safeKvGet
import com.seraphim.utils.safeKvSave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(private val repository: MealTaskRepository) : ViewModel() {
    private val _selectedDateFlow =
        MutableStateFlow(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))
    val selectedDateFlow = _selectedDateFlow.asStateFlow()

    private val _isLunchChecked = MutableStateFlow(false)
    val isLunchChecked = _isLunchChecked.asStateFlow()
    private val _isDinnerChecked = MutableStateFlow(false)
    val isDinnerChecked = _isDinnerChecked.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    val imageUrl = _imageUrl.asStateFlow()
    val monthString = YearMonth.now().toString()

    init {
        viewModelScope.launch {
            selectedDateFlow.collectLatest { day ->
                val task = repository.getTaskByDay(day.date.toString())
                    .map { it }
                    .firstOrNull()
                if (task == null) {
                    _isLunchChecked.value = false
                    _isDinnerChecked.value = false
                } else {
                    _isLunchChecked.value = task.isLunch
                    _isDinnerChecked.value = task.isDinner
                }
            }
        }
    }

    fun fetchImage() {
        val key = "local_image_date" + LocalDate.now().toString()
        val url = safeKvGet(key, "")
        if (url.isNotEmpty()) {
            _imageUrl.value = url
            return
        } else {
            viewModelScope.launch {
                val image = repository.getImage()
                _imageUrl.value = image
                image.safeKvSave(key)
            }
        }
//        viewModelScope.launch {
//            repository.getLocalImage().collectLatest { task ->
//                if (task?.image != null) {
//                    _imageUrl.value = task.image.orEmpty()
//                } else {
//                    val image = repository.getImage().image
//                    repository.setLocalImage(LocalDate.now().toString(), image)
//                    _imageUrl.value = image
//                }
//            }
//
//        }
    }

    suspend fun insertTask(day: String, isLunch: Boolean, isDinner: Boolean) {
        // 先查询是否存在记录
        val existingTask = repository.getTaskByDay(day)
            .map { it }
            .firstOrNull()
        if (existingTask == null) {
            // 不存在则插入新记录
            repository.insertTask(
                TaskEntity(
                    day,
                    monthString,
                    isLunch,
                    isDinner
                )
            )
        } else {
            // 存在则更新记录
            val updatedLunch = existingTask.isLunch || isLunch
            val updatedDinner = existingTask.isDinner || isDinner
            repository.updateLunch(day, updatedLunch)
            repository.updateDinner(day, updatedDinner)
        }
    }

    fun updateLunch(day: String, isLunch: Boolean) {
        viewModelScope.launch {
            val existingTask = repository.getTaskByDay(day)
                .map { it }
                .firstOrNull()
            if (existingTask == null) {
                // 不存在则插入新记录
                repository.insertTask(
                    TaskEntity(
                        day,
                        monthString,
                        isLunch,
                        false
                    )
                )
            } else {
                repository.updateLunch(day, isLunch)
            }
            _isLunchChecked.value = isLunch
        }
    }

    fun updateDinner(day: String, isDinner: Boolean) {
        viewModelScope.launch {

            val existingTask = repository.getTaskByDay(day)
                .map { it }
                .firstOrNull()
            if (existingTask == null) {
                // 不存在则插入新记录
                repository.insertTask(
                    TaskEntity(
                        day,
                        monthString,
                        false,
                        isDinner
                    )
                )
            } else {
                repository.updateDinner(day, isDinner)
            }
            _isDinnerChecked.value = isDinner
        }
    }

    fun getCountByDay(day: String) = repository.getTaskByDay(day).map {
        var count = 0
        if (it == null) {
            count = 0
        } else {
            if (it.isLunch) count++
            if (it.isDinner) count++
        }
        return@map count
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0
    )

    fun getTodayTask() = repository.getTaskByDay(LocalDate.now().toString()).map {
        if (it == null) {
            Pair(false, false)
        } else {
            Pair(it.isLunch, it.isDinner)
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Pair(false, false)
    )

    suspend fun deleteTaskByDay(day: String) = repository.deleteTaskByDay(day)

    fun getMonthlyMealTotal(month: String) = repository.getMonthlyMealTotal(month).stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0
    )

    fun updateSelectedDate(day: CalendarDay) {
        _selectedDateFlow.value = day
    }
}