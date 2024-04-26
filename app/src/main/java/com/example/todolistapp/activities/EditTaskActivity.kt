package com.example.todolistapp.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todolistapp.databinding.ActivityEditTaskBinding
import com.example.todolistapp.db.TaskDbHelper
import com.example.todolistapp.models.Task
import java.time.LocalDate

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var db: TaskDbHelper
    private var taskID: Int = -1
    private var dueDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)
        taskID = intent.getIntExtra("task_id", -1)
        if (taskId == -1){
            finish()
            return
        }

        val task = db.getTaskById(taskID)
        binding.editTitleEditText.setText(task?.title)
        binding.editDescriptionEditText.setText(task?.description)
        binding.editCompleteCheckBox.isChecked = task?.is_completed ?: false
        dueDate = task?.due_date
        binding.editDueDateButton.text = dueDate?.toString()

        binding.editDueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editSaveButton.setOnClickListener {
            val newTitle = binding.editTitleEditText.text.toString()
            val newDescription = binding.editDescriptionEditText.text.toString()
            val newIsComplete = binding.editCompleteCheckBox.isChecked

            val editedTask = Task(taskID, newTitle, newDescription, newIsComplete,
                dueDate
            )
            db.editTask(editedTask)

            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue - 1
        val day = currentDate.dayOfMonth

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                dueDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                binding.editDueDateButton.text = dueDate.toString()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}