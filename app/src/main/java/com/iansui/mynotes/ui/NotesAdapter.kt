package com.iansui.mynotes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.databinding.GridViewItemBinding

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

    class NoteViewHolder private constructor(private val binding: GridViewItemBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note) {
            binding.note = note
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridViewItemBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (note:Note) -> Unit) {
        fun onClick(note:Note) = clickListener(note)
    }
}