package com.example.plantsaverapplication.roomDatabase

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Krizbai Csaba
 * Created on 2022.11.26
 * Table name is: user_plants
 */

@Entity(tableName = "user_plants")
data class Plants(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date") var date: Long? = null, // Date when user added the flower
    @ColumnInfo(name = "cycle") var sprayingCycle: Int? = null, // Watering state stored as binary number. MSB is Monday and LSB is Sunday
    @ColumnInfo(name = "time") var time: String? = null, // At what time should you send alerts and notifications. Format as HH:MM
    @ColumnInfo(name = "name") var name: String? = null, // Plant name
    @ColumnInfo(name = "description") var description: String? = null, // Plant description
    @ColumnInfo(name = "image") val image: Bitmap? = null // Plant image
)

/**
 * DAO  = data access object, The DAO must be an interface or abstract class.
 * In these case we used interface.
 */
@Dao
interface PlantsDAO {

    /** Get all database value between two date */
    @Query("SELECT * FROM user_plants ORDER BY date ASC")
    fun getPlantsByDate(): LiveData<List<Plants>>

    /** Get one plant from database */
    @Query("SELECT * FROM user_plants WHERE id = :Id")
    suspend fun getPlantsById(Id: Int): Plants

    /** Insert new plants to database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newPlant: Plants): Long

    @Query("SELECT COUNT(*) FROM user_plants")
    suspend fun getPlantsNumber(): Int

    /** Delete all database */
    @Query("DELETE FROM user_plants WHERE id = :plantID")
    suspend fun deleteSelectedPlant(plantID: Int)

    /** Delete all database */
    @Query("DELETE FROM user_plants")
    suspend fun deleteAll()
}
