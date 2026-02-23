package com.ayushcodes.signupandloginusingfirebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayushcodes.signupandloginusingfirebase.databinding.NotesItemBinding

class NoteAdapter(private var notes: List<NoteItem>, private val itemClickListener: OnitemClickListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>()
{
    interface OnitemClickListener {
        fun onDeleteClick(noteID: String)
        fun onUpdateClick(noteID: String, title: String, description: String)
    }

    fun updateData(newNotes: List<NoteItem>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder
    {
       val binding = NotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int)
    {
        val note = notes[position]
        holder.bind(note)
        holder.binding.editButton.setOnClickListener {
            itemClickListener.onUpdateClick(note.noteID, note.title, note.description)
        }
        holder.binding.deleteButton.setOnClickListener {
            itemClickListener.onDeleteClick(note.noteID)
        }
    }

    override fun getItemCount(): Int
    {
        return notes.size
    }

    class NoteViewHolder(val binding: NotesItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(note: NoteItem)
        {
            binding.titleTextView.text = note.title
            binding.descriptionTextView.text = note.description
        }
    }
}