package com.kang.termproject2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class RecordExerciseActivity : AppCompatActivity() {

    private lateinit var selectedExercises: List<Exercise>
    private lateinit var exerciseContainer: LinearLayout
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_exercise)

        database = AppDatabase.getDatabase(this)
        exerciseContainer = findViewById(R.id.exerciseContainer)

        val exercisesJson = intent.getStringExtra("selected_exercises")
        selectedExercises = Gson().fromJson(exercisesJson, object : TypeToken<List<Exercise>>() {}.type)

        loadSelectedExercises()

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveExerciseRecords()
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        }
    }

    private fun loadSelectedExercises() {
        for (exercise in selectedExercises) {
            addExerciseInput(exercise)
        }
    }

    private fun addExerciseInput(exercise: Exercise) {
        val inflater = LayoutInflater.from(this)
        val exerciseView = inflater.inflate(R.layout.item_exercise_record, exerciseContainer, false)
        exerciseView.findViewById<TextView>(R.id.exerciseName).text = exercise.name

        val addSetButton = exerciseView.findViewById<Button>(R.id.addSetButton)
        val setsContainer = exerciseView.findViewById<LinearLayout>(R.id.setsContainer)

        addSetButton.setOnClickListener {
            val setView = inflater.inflate(R.layout.item_set_input, setsContainer, false)
            setsContainer.addView(setView)
        }

        exerciseContainer.addView(exerciseView)
    }

    private fun saveExerciseRecords() {
        for (i in 0 until exerciseContainer.childCount) {
            val exerciseView = exerciseContainer.getChildAt(i)
            val exerciseName = exerciseView.findViewById<TextView>(R.id.exerciseName).text.toString()
            val setsContainer = exerciseView.findViewById<LinearLayout>(R.id.setsContainer)

            val exerciseId = selectedExercises.first { it.name == exerciseName }.id

            for (j in 0 until setsContainer.childCount) {
                val setView = setsContainer.getChildAt(j)
                val weight = setView.findViewById<EditText>(R.id.weightInput).text.toString().toFloat()  // Float으로 유지
                val reps = setView.findViewById<EditText>(R.id.repsInput).text.toString().toInt()

                val exerciseRecord = ExerciseRecord(
                    exerciseId = exerciseId,
                    weight = weight,
                    reps = reps,
                    timestamp = System.currentTimeMillis()
                )

                lifecycleScope.launch {
                    database.exerciseRecordDao().insert(exerciseRecord)
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
