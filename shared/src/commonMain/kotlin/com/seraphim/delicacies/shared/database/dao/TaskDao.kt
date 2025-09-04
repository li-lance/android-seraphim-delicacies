package com.seraphim.delicacies.shared.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seraphim.delicacies.shared.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // 插入 / 更新任务（同一天记录覆盖）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    //根据day更新task
    @Query("UPDATE tasks SET isLunch = :isLunch WHERE day = :day")
    suspend fun updateLunchTask(day: String, isLunch: Boolean)

    @Query("UPDATE tasks SET isDinner = :isDinner WHERE day = :day")
    suspend fun updateDinnerTask(day: String, isDinner: Boolean)

    // 按天查询（day 格式 yyyy-MM-dd）
    @Query("SELECT * FROM tasks WHERE day = :day LIMIT 1")
    fun getTaskByDay(day: String): Flow<TaskEntity?>

    // 删除某天
    @Query("DELETE FROM tasks WHERE day = :day")
    suspend fun deleteTaskByDay(day: String)

    // 按月统计总次数（month 格式 yyyy-MM）午餐+晚餐各算一次
    @Query(
        """
        SELECT 
          COALESCE(SUM(CASE WHEN isLunch  THEN 1 ELSE 0 END),0) +
          COALESCE(SUM(CASE WHEN isDinner THEN 1 ELSE 0 END),0)
        FROM tasks
        WHERE day LIKE :month || '%'
        """
    )
    fun getMonthlyMealTotal(month: String): Flow<Int>
}
