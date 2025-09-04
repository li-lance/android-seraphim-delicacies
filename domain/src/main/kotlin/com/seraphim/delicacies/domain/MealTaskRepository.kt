package com.seraphim.delicacies.domain

import com.seraphim.delicacies.shared.database.dao.TaskDao
import com.seraphim.delicacies.shared.database.entity.TaskEntity
import kotlinx.coroutines.flow.map

class MealTaskRepository(dao: TaskDao) {
    private val taskDao = dao

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateLunch(day: String, isLunch: Boolean) {
        taskDao.updateLunchTask(day, isLunch)
    }

    suspend fun updateDinner(day: String, isDinner: Boolean) {
        taskDao.updateDinnerTask(day, isDinner)
    }

    fun getTaskByDay(day: String) = taskDao.getTaskByDay(day)

    fun getTaskByDayCount(day: String) = taskDao.getTaskByDay(day).map {
        var count = 0
        if (it?.isLunch == true) count++
        if (it?.isDinner == true) count++
        Pair(it, count)
    }

    suspend fun deleteTaskByDay(day: String) {
        taskDao.deleteTaskByDay(day)
    }

    fun getMonthlyMealTotal(month: String) = taskDao.getMonthlyMealTotal(month)
}