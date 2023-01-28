package com.example.plantsaverapplication.roomDatabase

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val TAG = "Database"

@Database(entities = [Plants::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseHandler: RoomDatabase() {

    abstract fun plantDao(): PlantsDAO

    companion object {
        /** Daily database */
        private var dInstance: DatabaseHandler? = null

        // If the instance is null, create the database else return him
        fun getInstance(context: Context): DatabaseHandler {
            return dInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHandler::class.java,
                    "user_plants"
                ).build()
                dInstance = instance
                instance
            }
        }
    }

    /** Read plant from database */
    suspend fun read(Id: Int): Plants {
        Log.i(TAG, "User read plant with ID: $Id")
        return plantDao().getPlantsById(Id)
    }


    /** Insert data to database */
    suspend fun insertPlants(newPlant: Plants): Long{
        Log.i(TAG, "User inserted new plants to database")
        return plantDao().insert(newPlant)
    }

    /** Get number of plants */
    suspend fun getPlantsNumber(): Int {
        return plantDao().getPlantsNumber()
    }

    /** Delete selected id */
    suspend fun deletePlant(ID: Int){
        plantDao().deleteSelectedPlant(ID)
        Log.i(TAG, "User deleted $ID plant")
    }


    /**Delete all database value */
    suspend fun deleteAll() {
        plantDao().deleteAll()
        Log.i(TAG, "User deleted all database components")
    }
}
