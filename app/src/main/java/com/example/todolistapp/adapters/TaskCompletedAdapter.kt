package com.example.todolistapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.activities.EditTaskActivity
import com.example.todolistapp.db.TaskDbHelper
import com.example.todolistapp.models.Task

class TaskCompletedAdapter(
    private var tasks: List<Task>,
    context: Context
) :
    RecyclerView.Adapter<TaskCompletedAdapter.TaskViewHolder>() {

    private val db: TaskDbHelper = TaskDbHelper(context)

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        private val completeCheckBox: CheckBox = itemView.findViewById(R.id.completeCheckBox)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)

        fun bind(task: Task) {
            titleTextView.text = task.title
            dueDateTextView.text = task.due_date.toString()

            // Apply strikethrough effect if the task is completed
            if (task.is_completed) {
                titleTextView.paintFlags = titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                completeCheckBox.isChecked = true // Set checkbox checked if task is completed
            } else {
                titleTextView.paintFlags =
                    titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                completeCheckBox.isChecked =
                    false // Set checkbox unchecked if task is not completed
            }

            // Set a listener for checkbox state changes
            completeCheckBox.setOnCheckedChangeListener { _, isChecked ->
                // Apply or remove strikethrough effect based on checkbox state
                if (isChecked) {
                    titleTextView.paintFlags =
                        titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    titleTextView.paintFlags =
                        titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                task.is_completed = isChecked
                db.editTask(task)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)

        holder.editButton.setOnClickListener {
            val i = Intent(holder.itemView.context, EditTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(i)
        }

        holder.deleteButton.setOnClickListener {
            val dlgView = AlertDialog.Builder(holder.itemView.context)
            dlgView.setTitle("Delete Task")
            dlgView.setMessage("Are you sure to delete this task")
            dlgView.setPositiveButton("Delete") { _, _ ->
                db.deleteTaskById(task.id)
                refreshData(db.getCompletedTasks())
                Toast.makeText(holder.itemView.context, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            dlgView.setNegativeButton("Cancel") { _, _ -> }
            val dlg = dlgView.create()
            dlg.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }


}