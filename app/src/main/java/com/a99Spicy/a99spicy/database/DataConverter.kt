package com.a99Spicy.a99spicy.database

import androidx.room.TypeConverter
import com.a99Spicy.a99spicy.network.CatImg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {

    @TypeConverter
    fun fromCategoryToString(category: List<DataBaseCategory>?): String? {
        if (category == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBaseCategory>?>() {}.type
        return gson.toJson(category, type)
    }

    @TypeConverter
    fun fromStringToCategory(category: String?): List<DataBaseCategory>? {
        if (category == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBaseCategory>?>() {}.type
        return gson.fromJson(category, type)
    }

    @TypeConverter
    fun fromMetaDataToString(metadatam: List<DataBaseMetaDatam>?): String? {
        if (metadatam == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBaseMetaDatam>?>() {}.type
        return gson.toJson(metadatam, type)
    }

    @TypeConverter
    fun fromStringToMetaData(string: String?): List<DataBaseMetaDatam>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBaseMetaDatam>?>() {}.type
        return gson.fromJson(string, type)
    }

    @TypeConverter
    fun fromImageToString(image: List<DataBasImage>?): String? {
        if (image == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBasImage>?>() {}.type
        return gson.toJson(image, type)
    }

    @TypeConverter
    fun fromStringToImage(string: String?): List<DataBasImage>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<DataBasImage>?>() {}.type
        return gson.fromJson(string, type)
    }

    @TypeConverter
    fun fromIntToString(ids: List<Int>?): String? {
        if (ids == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Int>?>() {}.type
        return gson.toJson(ids, type)
    }

    @TypeConverter
    fun fromStringToInt(ids: String?): List<Int>? {
        if (ids == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Int>?>() {}.type
        return gson.fromJson(ids, type)
    }

    @TypeConverter
    fun fromCatImageToString(image: CatImg?): String? {
        if (image == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<CatImg?>() {}.type
        return gson.toJson(image, type)
    }

    @TypeConverter
    fun fromStringToCatImage(string: String?): CatImg? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<CatImg?>() {}.type
        return gson.fromJson(string, type)
    }
}