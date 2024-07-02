package com.pam_228779.todoapppro.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pam_228779.todoapppro.databinding.ActivityTaskDetailBinding
import com.pam_228779.todoapppro.viewModel.TaskViewModel

class TaskDetailActivity : AppCompatActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var binding: ActivityTaskDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        // Załaduj zadanie z taskViewModel i zaktualizuj UI
        if (taskId != -1) {
            taskViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    binding.taskTitle.text = it.title
                    binding.taskDescription.text = it.description
                    // Inicjalizacja innych pól w interfejsie
                }
            }
        }
    }
}