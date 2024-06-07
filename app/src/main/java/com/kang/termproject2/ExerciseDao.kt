package com.kang.termproject2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE isDeleted = 0")
    suspend fun getAll(): List<Exercise>

    @Insert
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Query("UPDATE exercises SET isDeleted = 1 WHERE id = :exerciseId")
    suspend fun deleteById(exerciseId: Int)
}
