package com.example.plantsaverapplication.roomDatabase

import android.graphics.Bitmap
import androidx.room.*

/**
 * Krizbai Csaba
 * Created on 2022.11.26
 * Table name is: user_plants
 *
 * TODO: we need to manage user pictures
 */

@Entity(tableName = "user_plants")
data class Plants(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "date") val date: Long?, // Date when user added the flower
    @ColumnInfo(name = "cycle") val sprayingCycle: Int?, // Watering state stored as binary number. MSB is Monday and LSB is Sunday
    @ColumnInfo(name = "time") val time: String?, // At what time should you send alerts and notifications. Format as HH:MM
    @ColumnInfo(name = "name") val name: String?, // Plant name
    @ColumnInfo(name = "description") val description: String?, // Plant description
    @ColumnInfo(name = "image") val image: Bitmap? // Plant image
)

/**
 * DAO  = data access object, The DAO must be an interface or abstract class.
 */
@Dao
interface PlantsDAO {

    /** Get all database value between two date */
    @Query("SELECT * FROM user_plants ORDER BY date ASC")
    suspend fun getPlantsByDate(): List<Plants>

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
