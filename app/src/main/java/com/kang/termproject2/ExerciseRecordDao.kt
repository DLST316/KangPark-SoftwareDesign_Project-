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


}
