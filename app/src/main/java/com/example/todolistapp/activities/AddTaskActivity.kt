package com.example.todolistapp.activities

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.todolistapp.R
import com.example.todolistapp.databinding.ActivityAddTaskBinding
import com.example.todolistapp.db.TaskDbHelper
import com.example.todolistapp.models.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDbHelper
    private lateinit var dueDateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)
        dueDateButton = findViewById(R.id.dueDateButton)

        dueDateButton.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a DatePickerDialog and show it
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Format the selected date
                    val selectedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                    // Update the text of the editDueDateButton with the selected date
                    dueDateButton.text = selectedDate
                },
                year, month, dayOfMonth
            )



            // Show the DatePickerDialog
            datePickerDialog.show()
        }

        binding.saveButton.setOnClickListener{
            val title = binding.titleEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val dueDateText = binding.dueDateButton.text.toString()

            if (title.isBlank()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            } else {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val dueDate = if (dueDateText != "Select Due Date") {
                    LocalDate.parse(dueDateText, formatter)
                } else {
                    LocalDate.now()
                }

                val task = Task(0, title, description, false, dueDate)
                db.insertTask(task)
                finish()
                Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()
            }
        }

    }
}