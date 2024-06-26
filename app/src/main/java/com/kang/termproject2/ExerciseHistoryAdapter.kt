package com.kang.termproject2

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ExerciseHistoryAdapter(
    private var exerciseRecords: List<DetailedExerciseRecord>,
    private val context: Context,
    private val onDelete: (ExerciseSession) -> Unit
) : RecyclerView.Adapter<ExerciseHistoryAdapter.ViewHolder>() {

    private val expandedItems = mutableSetOf<Int>()
    private var groupedSessions: List<ExerciseSession> = listOf()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        groupedSessions = groupRecordsByDate(exerciseRecords)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val detailContainer: ViewGroup = itemView.findViewById(R.id.detailContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = groupedSessions[position]
        holder.timestamp.text = dateFormat.format(Date(session.timestamp))

        // Toggle visibility of detail container
        val isExpanded = expandedItems.contains(position)
        holder.detailContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.timestamp.setOnClickListener {
            if (isExpanded) {
                expandedItems.remove(position)
                notifyItemChanged(position)
            } else {
                expandedItems.add(position)
                notifyItemChanged(position)
            }
        }

        // Long click listener for deleting a specific session
        holder.timestamp.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("삭제 확인")
                .setMessage("이 날짜의 모든 운동 기록을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    onDelete(session)
                }
                .setNegativeButton("취소", null)
                .show()
            true
        }

        // Populate detail container
        if (isExpanded) {
            holder.detailContainer.removeAllViews()
            val inflater = LayoutInflater.from(holder.detailContainer.context)
            session.exercises.forEach { exercise ->
                val detailView = inflater.inflate(R.layout.item_exercise_detail, holder.detailContainer, false)
                detailView.findViewById<TextView>(R.id.exerciseName).text = exercise.name
                detailView.findViewById<TextView>(R.id.weightAndReps).text = exercise.records
                holder.detailContainer.addView(detailView)
            }
        }
    }

    override fun getItemCount() = groupedSessions.size

    fun updateExerciseRecords(newRecords: List<DetailedExerciseRecord>) {
        exerciseRecords = newRecords
        groupedSessions = groupRecordsByDate(newRecords)
        notifyDataSetChanged()
    }

    private fun groupRecordsByDate(records: List<DetailedExerciseRecord>): List<ExerciseSession> {
        val sortedRecords = records.sortedBy { it.timestamp }
        val sessions = mutableListOf<ExerciseSession>()
        var currentSession: ExerciseSession? = null

        sortedRecords.forEach { record ->
            if (currentSession == null || record.timestamp / 10000 != currentSession!!.timestamp / 10000) {
                currentSession = ExerciseSession(
                    timestamp = record.timestamp,
                    exercises = mutableListOf()
                )
                sessions.add(currentSession!!)
            }
            val exerciseDetail = currentSession!!.exercises.find { it.name == record.exerciseName }
            if (exerciseDetail != null) {
                exerciseDetail.records += "\n${record.weight} kg ${record.reps} 회"
            } else {
                currentSession!!.exercises.add(
                    ExerciseDetail(
                        name = record.exerciseName,
                        records = "${record.weight} kg ${record.reps} 회"
                    )
                )
            }
        }

        return sessions
    }
}

data class ExerciseSession(
    val timestamp: Long,
    val exercises: MutableList<ExerciseDetail>
)

data class ExerciseDetail(
    val name: String,
    var records: String
)
