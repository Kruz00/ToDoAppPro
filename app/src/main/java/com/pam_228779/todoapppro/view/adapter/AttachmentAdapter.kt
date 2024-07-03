package com.pam_228779.todoapppro.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pam_228779.todoapppro.databinding.ItemAttachmentBinding

class AttachmentAdapter(
    private val attachments: List<Pair<String, String>>, // filename, uri
    private val onAttachmentClick: (String) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(private val binding: ItemAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: String, filePath: String) {
            binding.attachmentTextView.text = attachment
            binding.attachmentTextView.setOnClickListener {
                onAttachmentClick(filePath)
            }
            binding.removeAttachmentButton.setOnClickListener {
                onRemoveClick(filePath)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val (attachment, filePath) = attachments[position]
        holder.bind(attachment, filePath)
    }

    override fun getItemCount(): Int = attachments.size
}
