package com.pam_228779.todoapppro.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pam_228779.todoapppro.databinding.ActivityTaskDetailBinding
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.view.adapter.AttachmentAdapter
import com.pam_228779.todoapppro.viewModel.TaskViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

private const val TAG = "TaskDetailActivity"

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var dueDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val attachments: MutableList<File> = mutableListOf()
    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var taskUniqueDir: String
    private lateinit var task: Task
    private var isBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            taskViewModel.getTaskById(taskId).observe(this) { task ->
                if (!isBound) {
                    this.task = task
                    this.taskUniqueDir = task.taskUniqueDir
                    bindTaskDetails(task)
                    isBound = true
                }
            }
        }

        binding.dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        binding.addAttachmentButton.setOnClickListener {
            selectAttachment()
        }

        binding.removeTaskButton.setOnClickListener {
            removeTask()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindTaskDetails(task: Task) {
        binding.titleEditText.setText(task.title)
        binding.descriptionEditText.setText(task.description)
        dueDate.time = task.dueAt
        updateDateInView()
        updateTimeInView()
        binding.categoryAutocomplete.setText(task.category, false)
        binding.completedSwitch.isChecked = task.isCompleted
        binding.notificationSwitch.isChecked = task.isNotificationEnabled
        attachments.addAll(task.attachmentFiles)
        attachmentAdapter.notifyDataSetChanged()
    }

    private fun showDatePickerDialog() {
        val year = dueDate.get(Calendar.YEAR)
        val month = dueDate.get(Calendar.MONTH)
        val day = dueDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            dueDate.set(Calendar.YEAR, selectedYear)
            dueDate.set(Calendar.MONTH, selectedMonth)
            dueDate.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateInView()
        }, year, month, day).show()
    }

    private fun showTimePickerDialog() {
        val hour = dueDate.get(Calendar.HOUR_OF_DAY)
        val minute = dueDate.get(Calendar.MINUTE)

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
                    val savedFile = saveAttachmentToExternalStorage(uri, fileName)
                    savedFile?.let {
                        attachments.add(savedFile)
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

    private fun saveAttachmentToExternalStorage(uri: Uri, fileName: String): File? {
        val taskDir = File(getExternalFilesDir(null), taskUniqueDir)
        if (!taskDir.exists()) {
            taskDir.mkdirs()
        }
        val destFile = File(taskDir, fileName)
        return try {
            contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isAttachmentDuplicate(fileName: String): Boolean {
        return attachments.any { it.name == fileName }
    }

    private fun setupAttachmentRecyclerView() {
        attachmentAdapter = AttachmentAdapter(attachments,
            onAttachmentClick = { attachment -> openAttachment(attachment) },
            onRemoveClick = { attachment -> removeAttachment(attachment) }
        )
        binding.attachmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.attachmentsRecyclerView.adapter = attachmentAdapter
    }

    private fun openAttachment(attachment: File) {
        val fileUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", attachment)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, contentResolver.getType(fileUri))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    private fun removeAttachment(attachment: File) {
        attachments.remove(attachments.find { it == attachment })
        deleteAttachmentFromExternalStorage(attachment)
        attachmentAdapter.notifyDataSetChanged()
    }

    private fun deleteAttachmentFromExternalStorage(attachment: File) {
        if (attachment.exists()) {
            attachment.delete()
        }
    }

    private fun updateDateInView() {
        binding.dueDateTextView.text = dateFormat.format(dueDate.time)
    }

    private fun updateTimeInView() {
        binding.dueTimeTextView.text = timeFormat.format(dueDate.time)
    }

    private fun removeTask() {
        val taskDir = File(getExternalFilesDir(null), taskUniqueDir)
        if (!taskDir.exists()) {
            taskDir.delete()
        }
        taskViewModel.delete(task)
        finish()
    }

    private fun saveTask() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val isNotificationEnabled = binding.notificationSwitch.isChecked
        val category = binding.categoryAutocomplete.text.toString()
        val isCompleted = binding.completedSwitch.isChecked

        if (title.isNotEmpty() && category.isNotEmpty()) {
            task.let {
                val updatedTask = it.copy(
                    title = title,
                    description = description,
                    dueAt = dueDate.time,
                    isCompleted = isCompleted,
                    isNotificationEnabled = isNotificationEnabled,
                    category = category,
                    hasAttachment = attachments.isNotEmpty(),
                    attachmentFiles = attachments,
                    taskUniqueDir = taskUniqueDir
                )
                if (updatedTask != task) {
                    Log.i(TAG, "Task '${task.title}' updated")
                    taskViewModel.update(updatedTask)
                }
                finish()
            }
        }
    }
}