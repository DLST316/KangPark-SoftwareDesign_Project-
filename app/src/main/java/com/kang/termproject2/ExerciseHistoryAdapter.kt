package com.kang.termproject2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class ExerciseHistoryAdapter(
    private var exerciseRecords: List<ExerciseRecord>
) : RecyclerView.Adapter<ExerciseHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val weight: TextView = itemView.findViewById(R.id.weight)
        val reps: TextView = itemView.findViewById(R.id.reps)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = exerciseRecords[position]
        holder.exerciseName.text = record.exerciseId.toString() // 실제 이름을 매핑하려면 추가 작업 필요
        holder.weight.text = record.weight.toString()
        holder.reps.text = record.reps.toString()
        holder.timestamp.text = Date(record.timestamp).toString()
    }

    override fun getItemCount() = exerciseRecords.size

    fun updateExerciseRecords(newRecords: List<ExerciseRecord>) {
        exerciseRecords = newRecords
        notifyDataSetChanged()
    }
}
