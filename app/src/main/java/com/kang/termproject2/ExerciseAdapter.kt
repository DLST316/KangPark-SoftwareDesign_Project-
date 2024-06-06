package com.kang.termproject2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(
    private var exercises: MutableList<Exercise>,
    private val onItemLongClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(exercise: Exercise, onItemLongClick: (Exercise) -> Unit) {
            view.findViewById<TextView>(R.id.templateName).text = exercise.name
            view.setOnLongClickListener {
                onItemLongClick(exercise)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position], onItemLongClick)
    }

    override fun getItemCount(): Int = exercises.size

    fun setItems(newItems: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(exercise: Exercise) {
        exercises.add(exercise)
        notifyItemInserted(exercises.size - 1)
    }

    fun removeItem(exercise: Exercise) {
        val position = exercises.indexOf(exercise)
        if (position >= 0) {
            exercises.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
