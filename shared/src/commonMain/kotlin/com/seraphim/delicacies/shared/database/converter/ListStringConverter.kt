package com.seraphim.delicacies.shared.database.converter

import androidx.room.TypeConverter
import com.seraphim.delicacies.shared.serializable.json

class ListStringConverter {
    @TypeConverter
    fun fromListString(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListString(value: String?): List<String>? {
        return value?.let { json.decodeFromString(it) }
    }
}