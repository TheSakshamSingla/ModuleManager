package com.saksham.modulemanager.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saksham.modulemanager.data.model.ModuleType
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromModuleType(value: ModuleType): String {
        return value.name
    }
    
    @TypeConverter
    fun toModuleType(value: String): ModuleType {
        return try {
            ModuleType.valueOf(value)
        } catch (e: Exception) {
            ModuleType.UNKNOWN
        }
    }
    
    @TypeConverter
    fun fromModuleTypeList(value: List<ModuleType>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toModuleTypeList(value: String): List<ModuleType> {
        val listType = object : TypeToken<List<ModuleType>>() {}.type
        return try {
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
