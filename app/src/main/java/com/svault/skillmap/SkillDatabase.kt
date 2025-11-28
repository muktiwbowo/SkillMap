package com.svault.skillmap

import android.app.Application
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [ModelSkill::class],
    version = 1,
    exportSchema = false
)
abstract class SkillDatabase : RoomDatabase() {
    companion object {
        fun initDatabase(application: Application) = Room.databaseBuilder(
            application, SkillDatabase::class.java, "skill_database"
        ).build()
    }

    abstract fun skillDao(): SkillDao
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM table_skill")
    fun getAllSkills(): Flow<List<ModelSkill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: ModelSkill)

    @Update
    suspend fun updateSkill(skill: ModelSkill)

    @Delete
    suspend fun deleteSkill(skill: ModelSkill)
}