package com.seraphim.delicacies.domain

import com.seraphim.delicacies.shared.database.dao.TaskDao
import com.seraphim.delicacies.shared.database.entity.TaskEntity
import com.seraphim.delicacies.shared.network.ApiService
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class MealTaskRepository(val dao: TaskDao,val api: ApiService) {
    suspend fun insertTask(task: TaskEntity): Long {
        return dao.insertTask(task)
    }

    suspend fun updateLunch(day: String, isLunch: Boolean) {
        dao.updateLunchTask(day, isLunch)
    }

    suspend fun updateDinner(day: String, isDinner: Boolean) {
        dao.updateDinnerTask(day, isDinner)
    }

    fun getTaskByDay(day: String) = dao.getTaskByDay(day)

    fun getTaskByDayCount(day: String) = dao.getTaskByDay(day).map {
        var count = 0
        if (it?.isLunch == true) count++
        if (it?.isDinner == true) count++
        Pair(it, count)
    }

    suspend fun deleteTaskByDay(day: String) {
        dao.deleteTaskByDay(day)
    }

    fun getMonthlyMealTotal(month: String) = dao.getMonthlyMealTotal(month)

    suspend fun getImage() = api.getImage()
}