package com.svault.skillmap

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_skill")
data class ModelSkill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "point_x")
    val pointX: Float,
    @ColumnInfo(name = "point_y")
    val pointY: Float,
    @ColumnInfo(name = "progress")
    val progress: Int
)
