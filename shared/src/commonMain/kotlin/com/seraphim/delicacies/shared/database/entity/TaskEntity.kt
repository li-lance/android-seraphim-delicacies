package com.seraphim.delicacies.shared.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val day: String,
    val month: String,
    val isLunch: Boolean,
    val isDinner: Boolean,
)