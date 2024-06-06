package com.kang.termproject2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ExerciseHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseHistoryAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_history)

        database = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadExerciseHistory()
    }

    private fun loadExerciseHistory() {
        lifecycleScope.launch {
            val exerciseRecords = database.exerciseRecordDao().getAll()
            adapter = ExerciseHistoryAdapter(exerciseRecords)
            recyclerView.adapter = adapter
        }
    }
}
