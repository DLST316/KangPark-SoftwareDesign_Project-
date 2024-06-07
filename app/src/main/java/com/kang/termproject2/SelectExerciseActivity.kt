package com.kang.termproject2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SelectExerciseActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: SelectableExerciseAdapter
    private val selectedExercises = mutableListOf<Exercise>()
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exercise)

        database = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.exercisesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = SelectableExerciseAdapter(mutableListOf()) { exercise, isSelected ->
            if (isSelected) {
                selectedExercises.add(exercise)
            } else {
                selectedExercises.remove(exercise)
            }
        }
        recyclerView.adapter = exerciseAdapter

        loadExercises()

        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            if (selectedExercises.isEmpty()) {
                Toast.makeText(this, "적어도 하나의 운동을 선택해야 합니다.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RecordExerciseActivity::class.java)
                intent.putExtra("selected_exercises", Gson().toJson(selectedExercises))
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.backToMainButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun loadExercises() {
        lifecycleScope.launch {//코루틴을 사용해서 UI반응성 높임
            val exercises = database.exerciseDao().getAll()
            exerciseAdapter.setItems(exercises)
        }
    }
}
