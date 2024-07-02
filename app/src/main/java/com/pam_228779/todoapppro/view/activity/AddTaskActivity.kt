package com.pam_228779.todoapppro.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.databinding.ActivityAddTaskBinding
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.viewModel.TaskViewModel
import com.pam_228779.todoapppro.utils.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var dueDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            saveTask()
        }

        taskViewModel.getAllCategories().observe(this) { categories ->
            val categoriesAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
            binding.categoryAutocomplete.setAdapter(categoriesAdapter)
        }

        binding.categoryAutocomplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.categoryAutocomplete.showDropDown()
            }
        }

        binding.categoryAutocomplete.setOnClickListener {
            binding.categoryAutocomplete.showDropDown()
        }

        updateDateInView()
        updateTimeInView()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            dueDate.set(Calendar.YEAR, selectedYear)
            dueDate.set(Calendar.MONTH, selectedMonth)
            dueDate.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateInView()
        }, year, month, day).show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            dueDate.set(Calendar.HOUR_OF_DAY, selectedHour)
            dueDate.set(Calendar.MINUTE, selectedMinute)
            updateTimeInView()
        }, hour, minute, true).show()
    }

    private fun saveTask() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val isNotificationEnabled = binding.notificationSwitch.isChecked
        val category = binding.categoryAutocomplete.text.toString()

        if (title.isNotEmpty() && category.isNotEmpty()) {
            val task = Task(
                title = title,
                description = description,
                createdAt = Calendar.getInstance().time,
                dueAt = dueDate.time,
                isCompleted = false,
                isNotificationEnabled = isNotificationEnabled,
                category = category,
                hasAttachment = false
            )
            taskViewModel.insert(task)
            finish()
        }
    }

    private fun updateDateInView() {
        binding.selectedDateText.text = dateFormat.format(dueDate.time)
    }

    private fun updateTimeInView() {
        binding.selectedTimeText.text = timeFormat.format(dueDate.time)
    }

}