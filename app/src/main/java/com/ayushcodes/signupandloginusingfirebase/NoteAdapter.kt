package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.annotation.SuppressLint // Imports the SuppressLint annotation to suppress lint warnings
import android.app.AlertDialog // Imports the AlertDialog class to display a dialog message with positive and negative buttons
import android.content.Intent // Imports the Intent class for starting new activities
import android.view.LayoutInflater // Imports the LayoutInflater class to inflate layout XML files
import android.view.ViewGroup // Imports the ViewGroup class, which is a container for views
import androidx.recyclerview.widget.RecyclerView // Imports the RecyclerView class for displaying lists of items
import com.ayushcodes.signupandloginusingfirebase.databinding.NotesItemBinding // Imports the generated binding class for the notes_item.xml layout

class NoteAdapter(private var notes: List<NoteItem>, private val itemClickListener: OnitemClickListener) // Defines the NoteAdapter class, which takes a list of notes and a click listener as input
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() // Extends the RecyclerView.Adapter class to create a custom adapter
{
    interface OnitemClickListener { // Defines an interface for handling item clicks
        fun onDeleteClick(noteID: String) // Declares a function to handle delete button clicks
    }

    @SuppressLint("NotifyDataSetChanged") // Suppresses the lint warning for using notifyDataSetChanged()
    fun updateData(newNotes: List<NoteItem>) { // Defines a function to update the list of notes
        notes = newNotes // Assigns the new list of notes to the notes variable
        notifyDataSetChanged() // Notifies the adapter that the data has changed, so it can refresh the UI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder // Overrides the onCreateViewHolder function to create a new view holder
    {
       val binding = NotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Inflates the notes_item.xml layout using view binding
        return NoteViewHolder(binding) // Returns a new NoteViewHolder instance with the inflated binding
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) // Overrides the onBindViewHolder function to bind data to a view holder
    {
        val note = notes[position] // Gets the note at the current position
        holder.bind(note) // Binds the note data to the view holder
        holder.binding.editButton.setOnClickListener { // Sets a click listener for the edit button
            val intent = Intent(holder.itemView.context, EditNote::class.java).apply { // Creates a new Intent to start the EditNote activity
                putExtra("noteId", note.noteID) // Adds the note ID as an extra to the intent
                putExtra("noteTitle", note.title) // Adds the note title as an extra to the intent
                putExtra("noteDescription", note.description) // Adds the note description as an extra to the intent
            }
            holder.itemView.context.startActivity(intent) // Starts the EditNote activity
        }
        holder.binding.deleteButton.setOnClickListener { // Sets a click listener for the delete button
            AlertDialog.Builder(holder.itemView.context) // Creates a new AlertDialog.Builder
                .setTitle("Delete Note") // Sets the title of the dialog
                .setMessage("Are you sure you want to delete this note?") // Sets the message of the dialog
                .setPositiveButton("Delete") { _, _ -> // Sets the positive button of the dialog
                    itemClickListener.onDeleteClick(note.noteID) // Calls the onDeleteClick function of the item click listener
                }
                .setNegativeButton("Cancel", null) // Sets the negative button of the dialog
                .show() // Shows the dialog
        }
    }

    override fun getItemCount(): Int // Overrides the getItemCount function to return the number of items in the list
    {
        return notes.size // Returns the size of the notes list
    }

    class NoteViewHolder(val binding: NotesItemBinding) : RecyclerView.ViewHolder(binding.root) // Defines the NoteViewHolder class, which holds the views for a single item in the list
    {
        fun bind(note: NoteItem) // Defines a function to bind note data to the views
        {
            binding.titleTextView.text = note.title // Sets the text of the title text view to the note's title
            binding.descriptionTextView.text = note.description // Sets the text of the description text view to the note's description
            binding.dateTextView.text = note.date // Sets the text of the date text view to the note's date
        }
    }
}