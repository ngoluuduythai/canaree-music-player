package dev.olog.data.db

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import dev.olog.data.di.NetworkModule
import dev.olog.data.model.db.EqualizerBandEntity

internal object CustomTypeConverters {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return NetworkModule.gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromArrayList(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return NetworkModule.gson.toJson(list, listType)
    }
}