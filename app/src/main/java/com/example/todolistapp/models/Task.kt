package com.example.todolistapp.models

import java.time.LocalDate

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    var is_completed: Boolean,
    val due_date: LocalDate?
)