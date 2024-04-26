package com.example.todolistapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.activities.AddTaskActivity
import com.example.todolistapp.activities.CompleteTaskActivity
import com.example.todolistapp.adapters.TaskAdapter
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.db.TaskDbHelper
import com.example.todolistapp.models.Task

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: TaskDbHelper
    private lateinit var adapter: TaskAdapter
    private var myMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)
        adapter = TaskAdapter(db.getNotCompletedTasks(), this)

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter

        binding.addButton.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            startActivity(i)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.myMenu = menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        if(menu is MenuBuilder){
            menu.setOptionalIconsVisible(true)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnuCompletedTask -> {
                // Chuyển sang Activity hiển thị danh sách các task đã hoàn thành
                startActivity(Intent(this, CompleteTaskActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refreshData(db.getNotCompletedTasks())
    }

}