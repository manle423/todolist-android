package com.example.todolistapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolistapp.models.Task
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TaskDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "todo_list_app_db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "tasks"
        private const val id_col = "id"
        private const val title_col = "title"
        private const val description_col = "description"
        private const val is_completed_col = "status"
        private const val due_date_col = "due_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTaskTable = "CREATE TABLE $TABLE_NAME" +
                "($id_col INTEGER PRIMARY KEY," +
                "$title_col TEXT," +
                "$description_col TEXT," +
                "$is_completed_col INTEGER," +
                "$due_date_col TEXT)"
        db?.execSQL(createTaskTable)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTaskTable = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTaskTable)
        onCreate(db)
    }

    //#region Insert Task
    fun insertTask(task: Task) {
        val db = writableDatabase
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val values = ContentValues().apply {
            put(title_col, task.title)
            put(description_col, task.description)
            put(is_completed_col, if (task.is_completed) 1 else 0)
            task.due_date?.let { put(due_date_col, it.format(formatter)) }
        }
        db.insert(TABLE_NAME, null, values)
//        db.close()
    }

    //#endregion

    //#region Get All Tasks
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(id_col))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(title_col))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(description_col))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(is_completed_col)) == 1
            val dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(due_date_col))

            // Parse due_date string back to LocalDate object
            val dueDate = LocalDate.parse(dueDateStr, formatter)

            val task = Task(id, title, description, isCompleted, dueDate)
            taskList.add(task)
        }
        cursor.close()
        db.close()
        return taskList
    }
    //#endregion

    //#region Edit Task
    fun editTask(task: Task) {
        val db = writableDatabase
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val values = ContentValues().apply {
            put(title_col, task.title)
            put(description_col, task.description)
            put(is_completed_col, if (task.is_completed) 1 else 0)
            task.due_date?.let { put(due_date_col, it.format(formatter)) }
        }

        val whereClause = "$id_col = ?"
        val whereArgs = arrayOf(task.id.toString())

        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    //#endregion

    //#region Get Task By Id
    @SuppressLint("Recycle")
    fun getTaskById(taskId: Int): Task? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $id_col = ?"
        val cursor = db.rawQuery(query, arrayOf(taskId.toString()))

        var task: Task? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(id_col))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(title_col))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(description_col))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(is_completed_col)) == 1

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(due_date_col))
            val dueDate = LocalDate.parse(dueDateStr, formatter)

            task = Task(id, title, description, isCompleted, dueDate)
        }

        cursor.close()
        db.close()
        return task
    }

    //#endregion

    //#region Delete Task
    fun deleteTaskById(taskId: Int) {
        val db = writableDatabase
        val whereClause = "$id_col = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
    //#endregion

    //#region Check All Task Complete
    fun checkAllTasksCompleted(): Boolean {
        val allTasks = this.getAllTasks()
        for (task in allTasks) {
            if (!task.is_completed) {
                return false
            }
        }
        return true
    }
    //#endregion

    //#region Get Completed Tasks
    fun getCompletedTasks(): List<Task> {
        val completedTasks = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $is_completed_col = 1"
        val cursor = db.rawQuery(query, null)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(id_col))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(title_col))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(description_col))
            val isCompleted = true
            val dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(due_date_col))
            val dueDate = LocalDate.parse(dueDateStr, formatter)

            val task = Task(id, title, description, isCompleted, dueDate)
            completedTasks.add(task)
        }

        cursor.close()
        db.close()
        return completedTasks
    }
    //#endregion

    //#region Get Not Completed Tasks
    fun getNotCompletedTasks(): List<Task> {
        val notCompletedTasks = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $is_completed_col = 0"
        val cursor = db.rawQuery(query, null)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(id_col))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(title_col))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(description_col))
            val isCompleted = false
            val dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(due_date_col))
            val dueDate = LocalDate.parse(dueDateStr, formatter)

            val task = Task(id, title, description, isCompleted, dueDate)
            notCompletedTasks.add(task)
        }

        cursor.close()
        db.close()
        return notCompletedTasks
    }
    //#endregion

}