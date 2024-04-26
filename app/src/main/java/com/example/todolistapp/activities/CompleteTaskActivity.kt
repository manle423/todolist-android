package com.example.todolistapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.adapters.TaskAdapter
import com.example.todolistapp.adapters.TaskCompletedAdapter
import com.example.todolistapp.databinding.ActivityCompleteTaskBinding
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.db.TaskDbHelper
import com.example.todolistapp.models.Task

class CompleteTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteTaskBinding
    private lateinit var db: TaskDbHelper
    private lateinit var taskAdapter: TaskCompletedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)
        taskAdapter = TaskCompletedAdapter(db.getCompletedTasks(), this)

        binding.taskCompleteRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskCompleteRecyclerView.adapter = taskAdapter

        binding.backButton.setOnClickListener {
            finish()
        }
    }

}
