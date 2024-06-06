package com.kang.termproject2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SelectExerciseActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private val selectedExercises = mutableListOf<Exercise>()
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exercise)

        database = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.exercisesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = ExerciseAdapter(mutableListOf()) { exercise, isSelected ->
            if (isSelected) {
                selectedExercises.add(exercise)
            } else {
                selectedExercises.remove(exercise)
            }
        }
        recyclerView.adapter = exerciseAdapter

        loadExercises()

        val confirmButton = findViewById<Button>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val intent = Intent(this, RecordExerciseActivity::class.java)
            intent.putExtra("selected_exercises", Gson().toJson(selectedExercises))
            startActivity(intent)
        }
    }

    private fun loadExercises() {
        lifecycleScope.launch {
            val exercises = database.exerciseDao().getAll()
            exerciseAdapter.updateExercises(exercises)
        }
    }
}
