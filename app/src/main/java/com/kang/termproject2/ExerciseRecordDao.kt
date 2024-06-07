package com.kang.termproject2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseRecordDao {
    @Query("SELECT * FROM exercise_records")
    suspend fun getAll(): List<ExerciseRecord>

    @Insert
    suspend fun insert(exerciseRecord: ExerciseRecord)

    @Query("""
        SELECT exercise_records.id, exercises.name AS exerciseName, exercise_records.weight, exercise_records.reps, exercise_records.timestamp
        FROM exercise_records
        INNER JOIN exercises ON exercise_records.exerciseId = exercises.id
    """)
    suspend fun getAllDetailedRecords(): List<DetailedExerciseRecord>
}
