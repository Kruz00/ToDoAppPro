package com.pam_228779.todoapppro.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pam_228779.todoapppro.databinding.ItemAttachmentBinding
import java.io.File

class AttachmentAdapter(
    private val attachments: List<File>,
    private val onAttachmentClick: (File) -> Unit,
    private val onRemoveClick: (File) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(private val binding: ItemAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: File) {
            binding.attachmentTextView.text = attachment.name
            binding.attachmentTextView.setOnClickListener {
                onAttachmentClick(attachment)
            }
            binding.removeAttachmentButton.setOnClickListener {
                onRemoveClick(attachment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val attachment = attachments[position]
        holder.bind(attachment)
    }

    override fun getItemCount(): Int = attachments.size
}
