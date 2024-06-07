package com.kang.termproject2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class RecordExerciseActivity : AppCompatActivity() {

    private lateinit var selectedExercises: MutableList<Exercise>
    private lateinit var exerciseContainer: LinearLayout
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_exercise)

        database = AppDatabase.getDatabase(this)
        exerciseContainer = findViewById(R.id.exerciseContainer)

        val exercisesJson = intent.getStringExtra("selected_exercises")
        selectedExercises = Gson().fromJson(exercisesJson, object : TypeToken<MutableList<Exercise>>() {}.type)

        loadSelectedExercises()

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            if (hasEmptyFields()) {
                showEmptyFieldsDialog()
            } else {
                saveExerciseRecords()
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            }
        }

        findViewById<Button>(R.id.addExerciseButton).setOnClickListener {
            showAddExerciseDialog()
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
        val deleteExerciseButton = exerciseView.findViewById<Button>(R.id.deleteExerciseButton)
        val setsContainer = exerciseView.findViewById<LinearLayout>(R.id.setsContainer)

        addSetButton.setOnClickListener {
            val setView = inflater.inflate(R.layout.item_set_input, setsContainer, false)
            setView.findViewById<Button>(R.id.deleteSetButton).setOnClickListener {
                setsContainer.removeView(setView)
            }
            setsContainer.addView(setView)
        }

        deleteExerciseButton.setOnClickListener {
            if (exerciseContainer.childCount > 1) {
                exerciseContainer.removeView(exerciseView)
                selectedExercises = selectedExercises.filter { it.name != exercise.name }.toMutableList()
            } else {
                Toast.makeText(this, "운동을 하나 이상 남겨두어야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        exerciseContainer.addView(exerciseView)
    }

    private fun hasEmptyFields(): Boolean {
        for (i in 0 until exerciseContainer.childCount) {
            val exerciseView = exerciseContainer.getChildAt(i)
            val setsContainer = exerciseView.findViewById<LinearLayout>(R.id.setsContainer)
            for (j in 0 until setsContainer.childCount) {
                val setView = setsContainer.getChildAt(j)
                val weightInput = setView.findViewById<EditText>(R.id.weightInput).text.toString()
                val repsInput = setView.findViewById<EditText>(R.id.repsInput).text.toString()
                if (weightInput.isBlank() || repsInput.isBlank()) {
                    return true
                }
            }
        }
        return false
    }

    private fun showEmptyFieldsDialog() {
        AlertDialog.Builder(this)
            .setTitle("빈 입력 필드")
            .setMessage("빈칸인 부분은 기록이 전혀 되지 않습니다. 계속하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                saveExerciseRecords(skipEmptyFields = true)
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun saveExerciseRecords(skipEmptyFields: Boolean = false) {
        for (i in 0 until exerciseContainer.childCount) {
            val exerciseView = exerciseContainer.getChildAt(i)
            val exerciseName = exerciseView.findViewById<TextView>(R.id.exerciseName).text.toString()
            val setsContainer = exerciseView.findViewById<LinearLayout>(R.id.setsContainer)

            val exerciseId = selectedExercises.first { it.name == exerciseName }.id

            for (j in 0 until setsContainer.childCount) {
                val setView = setsContainer.getChildAt(j)
                val weightInput = setView.findViewById<EditText>(R.id.weightInput).text.toString()
                val repsInput = setView.findViewById<EditText>(R.id.repsInput).text.toString()

                if (skipEmptyFields && (weightInput.isBlank() || repsInput.isBlank())) {
                    continue
                }

                val weight = weightInput.toFloatOrNull() ?: continue
                val reps = repsInput.toIntOrNull() ?: continue

                val exerciseRecord = ExerciseRecord(
                    exerciseId = exerciseId,
                    weight = weight,
                    reps = reps,
                    timestamp = System.currentTimeMillis()
                )

                lifecycleScope.launch {//코루틴
                    database.exerciseRecordDao().insert(exerciseRecord)
                }
            }
        }
    }

    private fun showAddExerciseDialog() {
        lifecycleScope.launch {// 코루틴사용
            val allExercises = database.exerciseDao().getAll()
            val availableExercises = allExercises.filter { exercise ->
                selectedExercises.none { it.name == exercise.name }
            }

            if (availableExercises.isEmpty()) {
                Toast.makeText(this@RecordExerciseActivity, "추가할 수 있는 운동이 없습니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val exerciseNames = availableExercises.map { it.name }.toTypedArray()

            AlertDialog.Builder(this@RecordExerciseActivity)
                .setTitle("운동 추가")
                .setItems(exerciseNames) { _, which ->
                    val selectedExercise = availableExercises[which]
                    selectedExercises.add(selectedExercise)
                    addExerciseInput(selectedExercise)
                }
                .show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
