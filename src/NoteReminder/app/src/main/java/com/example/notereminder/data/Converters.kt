package com.example.notereminder.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    /**
     * convert Date to Long
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * convert Long to Date
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
