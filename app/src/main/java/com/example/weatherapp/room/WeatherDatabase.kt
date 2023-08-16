package com.example.weatherapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class],version=1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun roomDao() : RoomDao

    companion object {
        private const val DB_NAME = "weather_database.db"
        @Volatile private var instance: WeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            DB_NAME
        ).build()
    }

}