package com.pam_228779.todoapppro.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pam_228779.todoapppro.databinding.ActivityAddTaskBinding
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.view.adapter.AttachmentAdapter
import com.pam_228779.todoapppro.viewModel.TaskViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var dueDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val attachments: MutableList<Pair<String, String>> = mutableListOf() // file name, file path
    private lateinit var attachmentAdapter: AttachmentAdapter
    private var taskUniqueId: String = UUID.randomUUID().toString()

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

        binding.addAttachmentButton.setOnClickListener {
            selectAttachment()
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

        setupAttachmentRecyclerView()
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

    private fun selectAttachment() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        selectAttachmentLauncher.launch(intent)
    }

    private val selectAttachmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.also { uri ->
                val fileName = getFileName(uri)
                if (!isAttachmentDuplicate(fileName)) {
                    val savedPath = saveAttachmentToExternalStorage(uri, fileName)
                    savedPath?.let {
                        attachments.add(Pair(fileName, it))
                        attachmentAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this, "This attachment is already added.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun saveAttachmentToExternalStorage(uri: Uri, fileName: String): String? {
        val taskDir = File(getExternalFilesDir(null), taskUniqueId)
        if (!taskDir.exists()) {
            taskDir.mkdirs()
        }
        val destFile = File(taskDir, fileName)

        return try {
            contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isAttachmentDuplicate(fileName: String): Boolean {
        return attachments.any { it.first == fileName }
    }

    private fun setupAttachmentRecyclerView() {
        attachmentAdapter = AttachmentAdapter(attachments) { attachmentPath ->
            attachments.remove(attachments.find { it.second == attachmentPath })
            deleteAttachmentFromExternalStorage(attachmentPath)
            attachmentAdapter.notifyDataSetChanged()
        }
        binding.attachmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.attachmentsRecyclerView.adapter = attachmentAdapter
    }

    private fun deleteAttachmentFromExternalStorage(attachmentPath: String) {
        val file = File(attachmentPath)
        if (file.exists()) {
            file.delete()
        }
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
                hasAttachment = attachments.isNotEmpty(),
                attachmentUris = attachments.map { it.second }
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