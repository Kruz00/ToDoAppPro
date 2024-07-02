package com.pam_228779.todoapppro.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.viewModel.TaskViewModel
import com.pam_228779.todoapppro.utils.TaskReminderWorker
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class AddTaskActivity : AppCompatActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private var chosenDueDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val editTextTitle = findViewById<EditText>(R.id.edit_text_title)
        val editTextDescription = findViewById<EditText>(R.id.edit_text_description)
        val textViewDueDate = findViewById<TextView>(R.id.text_view_due_date)
        val buttonSave = findViewById<Button>(R.id.button_save)

        textViewDueDate.setOnClickListener {
            showDateTimePickerDialog { date ->
                chosenDueDate = date
                textViewDueDate.text = date.toString()
            }
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val dueDate = if (chosenDueDate == null) {
                Toast.makeText(applicationContext, "Due date not set!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                chosenDueDate!!
            }

            val task = Task(
                title = title,
                description = description,
                createdAt = Date(),
                dueAt = dueDate,
                category = "General"
            )

            taskViewModel.insert(task)
            finish()
        }
    }

    private fun showDateTimePickerDialog(onDateSet: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hour,
                    minute
                    )
                onDateSet(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        datePickerDialog.show()
        timePickerDialog.show()
    }
}