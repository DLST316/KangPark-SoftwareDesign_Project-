package com.kang.termproject2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectableExerciseAdapter(
    private var exercises: MutableList<Exercise>,
    private val onSelect: (Exercise, Boolean) -> Unit
) : RecyclerView.Adapter<SelectableExerciseAdapter.ExerciseViewHolder>() {

    private var selectedPositions = HashSet<Int>()

    class ExerciseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(exercise: Exercise, isSelected: Boolean, onSelect: (Exercise, Boolean) -> Unit) {
            view.findViewById<TextView>(R.id.templateName).text = exercise.name
            view.findViewById<CheckBox>(R.id.checkBox).apply {
                isChecked = isSelected
                setOnCheckedChangeListener { _, isChecked ->
                    onSelect(exercise, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_selectable, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val isSelected = selectedPositions.contains(position)
        holder.bind(exercises[position], isSelected) { exercise, isChecked ->
            if (isChecked) {
                selectedPositions.add(position)
            } else {
                selectedPositions.remove(position)
            }
            onSelect(exercise, isChecked)
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun setItems(newItems: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newItems)
        notifyDataSetChanged()
    }
}
