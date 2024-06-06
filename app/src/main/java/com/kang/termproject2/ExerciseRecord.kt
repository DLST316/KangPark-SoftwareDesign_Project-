package com.kang.termproject2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,  // Exercise 엔티티와의 연관성을 위해 exerciseId 추가
    val weight: Float,
    val reps: Int,
    val timestamp: Long
)
