package com.kang.termproject2

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kang.termproject2.databinding.ActivityListBinding
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var adapter: ExerciseAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ExerciseAdapter(mutableListOf()) { exercise -> showDeleteConfirmationDialog(exercise) }
        recyclerView.adapter = adapter

        binding.addButton.setOnClickListener {
            showAddItemDialog()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val exercises = database.exerciseDao().getAll()
            adapter.setItems(exercises)
        }
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Exercise")

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val input = view.findViewById<EditText>(R.id.editTextItemName)

        builder.setView(view)
        builder.setPositiveButton("Add") { dialog, _ ->
            val itemName = input.text.toString()
            if (itemName.isNotBlank()) {
                addItem(Exercise(name = itemName))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addItem(exercise: Exercise) {
        lifecycleScope.launch {
            database.exerciseDao().insert(exercise)
            adapter.addItem(exercise)
        }
    }

    private fun showDeleteConfirmationDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteItem(exercise)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun deleteItem(exercise: Exercise) {
        lifecycleScope.launch {
            database.exerciseDao().delete(exercise)
            adapter.removeItem(exercise)
        }
    }
}
