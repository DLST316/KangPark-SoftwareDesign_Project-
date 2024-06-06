package com.kang.termproject2

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecordExerciseActivity : AppCompatActivity() {

    private lateinit var selectedExercises: List<Exercise>
    private lateinit var exerciseContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_exercise)

        exerciseContainer = findViewById(R.id.exerciseContainer)

        val exercisesJson = intent.getStringExtra("selected_exercises")
        selectedExercises = Gson().fromJson(exercisesJson, object : TypeToken<List<Exercise>>() {}.type)

        loadSelectedExercises()

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            // 데이터 저장 로직 추가
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
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
}
