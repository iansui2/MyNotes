package com.iansui.mynotes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iansui.mynotes.R
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.databinding.NoteViewItemBinding

class NotesAdapter(private val onClickListener: OnClickListener): ListAdapter<Note, NotesAdapter.NoteViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(note)
        }
        holder.bind(note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.from(parent)
    }

    class NoteViewHolder private constructor(private val binding: NoteViewItemBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note) {
            binding.note = note
            when (note.color) {
                "blue" -> {
                    binding.relativeLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blue))
                }
                "green" -> {
                    binding.relativeLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                }
                "yellow" -> {
                    binding.relativeLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
                }
                "light_yellow" -> {
                    binding.relativeLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.yellow_200))
                }
                "pink" -> {
                    binding.relativeLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.pink))
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NoteViewItemBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (note:Note) -> Unit) {
        fun onClick(note:Note) = clickListener(note)
    }
}