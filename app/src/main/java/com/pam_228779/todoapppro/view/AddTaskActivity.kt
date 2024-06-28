package com.pam_228779.todoapppro.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.viewModel.TaskViewModel
import java.util.Calendar
import java.util.Date

class AddTaskActivity : AppCompatActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val editTextTitle = findViewById<EditText>(R.id.edit_text_title)
        val editTextDescription = findViewById<EditText>(R.id.edit_text_description)
        val textViewDueDate = findViewById<TextView>(R.id.text_view_due_date)
        val buttonSave = findViewById<Button>(R.id.button_save)

        textViewDueDate.setOnClickListener {
            showDatePickerDialog { date ->
                textViewDueDate.text = date.toString()  // Zapisz wybraną datę jako tekst
            }
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val dueDate = Date()  // Zamień na wybraną datę z textViewDueDate

            val task = Task(
                title = title,
                description = description,
                createdAt = Date(),
                dueAt = dueDate,
                category = "General"  // Przykładowa kategoria, można dodać więcej pól
            )

            taskViewModel.insert(task)
            finish()
        }
    }

    private fun showDatePickerDialog(onDateSet: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSet(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}