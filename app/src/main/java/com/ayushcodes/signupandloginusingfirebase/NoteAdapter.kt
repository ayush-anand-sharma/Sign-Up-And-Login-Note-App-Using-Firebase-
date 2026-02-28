package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.annotation.SuppressLint // Imports the SuppressLint annotation to suppress lint warnings
import android.app.AlertDialog // Imports the AlertDialog class to display a dialog message with positive and negative buttons
import android.content.Context // Imports the Context class for accessing application-specific resources and classes
import android.content.Intent // Imports the Intent class for starting new activities
import android.graphics.Color // Imports Color class for setting tint
import android.net.ConnectivityManager // Imports the ConnectivityManager class for checking network connectivity
import android.net.NetworkCapabilities // Imports the NetworkCapabilities class for checking network capabilities
import android.view.LayoutInflater // Imports the LayoutInflater class to inflate layout XML files
import android.view.ViewGroup // Imports the ViewGroup class, which is a container for views
import android.widget.Toast // Imports the Toast class for showing short messages
import androidx.core.content.ContextCompat // Imports ContextCompat for resource handling
import androidx.recyclerview.widget.RecyclerView // Imports the RecyclerView class for displaying lists of items
import com.ayushcodes.signupandloginusingfirebase.databinding.NotesItemBinding // Imports the generated binding class for the notes_item.xml layout

class NoteAdapter(private var notes: List<NoteItem>, private val itemClickListener: OnitemClickListener) // Defines the NoteAdapter class, which takes a list of notes and a click listener as input
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() // Extends the RecyclerView.Adapter class to create a custom adapter
{
    interface OnitemClickListener { // Defines an interface for handling item clicks
        fun onEditClick(noteID: String) // Declares a function to handle edit button clicks
        fun onDeleteClick(noteID: String) // Declares a function to handle delete button clicks
        fun onPinClick(noteID: String, isPinned: Boolean) // Declares a function to handle pin button clicks
        fun onNoteClick(note: NoteItem) // Declares a function to handle clicks on the entire note item
    }

    @SuppressLint("NotifyDataSetChanged") // Suppresses the lint warning for using notifyDataSetChanged()
    fun updateData(newNotes: List<NoteItem>) { // Defines a function to update the list of notes
        notes = newNotes // Assigns the new list of notes to the notes variable
        notifyDataSetChanged() // Notifies the adapter that the data has changed, so it can refresh the UI
    }

    // Finds and returns a note by its ID from the current list of notes.
    fun getNote(noteID: String): NoteItem? { // Defines a function to get a note by its ID.
        return notes.find { it.noteID == noteID } // Returns the first note that matches the provided noteID, or null if not found.
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

        // Sets a click listener for the entire note item
        holder.itemView.setOnClickListener {
            itemClickListener.onNoteClick(note) // Calls the onNoteClick function of the item click listener
        }

        // Set OnClickListener for the edit button
        holder.binding.editButton.setOnClickListener { // Sets a click listener for the edit button
            if (isNetworkAvailable(holder.itemView.context)) { // Checks if a network connection is available
                itemClickListener.onEditClick(note.noteID)
            } else { // If no network connection is available
                Toast.makeText(holder.itemView.context, "No internet connection", Toast.LENGTH_SHORT).show() // Shows a toast message
            }
        }

        // Set OnClickListener for the delete button
        holder.binding.deleteButton.setOnClickListener { // Sets a click listener for the delete button
            if (isNetworkAvailable(holder.itemView.context)) { // Checks if a network connection is available
                AlertDialog.Builder(holder.itemView.context) // Creates a new AlertDialog.Builder
                    .setTitle("Delete Note") // Sets the title of the dialog
                    .setMessage("Are you sure you want to delete this note?") // Sets the message of the dialog
                    .setPositiveButton("Delete") { _, _ -> // Sets the positive button of the dialog
                        itemClickListener.onDeleteClick(note.noteID) // Calls the onDeleteClick function
                    }
                    .setNegativeButton("Cancel", null) // Sets the negative button of the dialog
                    .show() // Shows the dialog
            } else { // If no network connection is available
                Toast.makeText(holder.itemView.context, "No internet connection", Toast.LENGTH_SHORT).show() // Shows a toast message
            }
        }

        // Set OnClickListener for the pin button
        holder.binding.pinButton.setOnClickListener { // Sets a click listener for the pin button
            if (isNetworkAvailable(holder.itemView.context)) { // Checks if a network connection is available
                // Toggle the state and notify listener
                val newPinnedStatus = !note.pinned
                itemClickListener.onPinClick(note.noteID, newPinnedStatus)
            } else { // If no network connection is available
                Toast.makeText(holder.itemView.context, "No internet connection", Toast.LENGTH_SHORT).show() // Shows a toast message
            }
        }
    }

    override fun getItemCount(): Int // Overrides the getItemCount function to return the number of items in the list
    {
        return notes.size // Returns the size of the notes list
    }

    private fun isNetworkAvailable(context: Context): Boolean { // Defines a function to check for an active internet connection
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets an instance of the ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false // Gets the currently active network, or returns false if there is none
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false // Gets the capabilities of the active network, or returns false if they cannot be determined
        return when { // Returns true if the network has any of the following transport types
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // The network is a Wi-Fi network
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // The network is a cellular network
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true // The network is an Ethernet network
            else -> false // The network does not have any of the supported transport types
        }
    }

    class NoteViewHolder(val binding: NotesItemBinding) : RecyclerView.ViewHolder(binding.root) // Defines the NoteViewHolder class
    {
        fun bind(note: NoteItem) // Defines a function to bind note data to the views
        {
            binding.titleTextView.text = note.title // Sets the text of the title text view to the note's title
            binding.descriptionTextView.text = note.description // Sets the text of the description text view to the note's description
            binding.dateTextView.text = note.date // Sets the text of the date text view to the note's date

            // Appearance Logic: Set icon and color based on pinned state
            if (note.pinned) {
                // If pinned: use filled star and gold/yellow color
                binding.pinButton.setImageResource(android.R.drawable.btn_star_big_on)
                binding.pinButton.setColorFilter(Color.parseColor("#FFD700")) // Gold color
            } else {
                // If not pinned: use empty star and white color
                binding.pinButton.setImageResource(android.R.drawable.btn_star_big_off)
                binding.pinButton.setColorFilter(Color.WHITE)
            }
        }
    }
}