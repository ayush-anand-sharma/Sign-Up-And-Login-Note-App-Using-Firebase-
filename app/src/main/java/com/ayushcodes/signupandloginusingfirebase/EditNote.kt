package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.os.Bundle // Imports the Bundle class for saving instance state
import android.widget.Toast // Imports the Toast class for showing short messages
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function for enabling edge-to-edge display
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class for compatibility features
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityEditNoteBinding // Imports the binding class for the activity_edit_note.xml layout
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class for user authentication
import com.google.firebase.database.DatabaseReference // Imports the DatabaseReference class for interacting with the Firebase Realtime Database
import com.google.firebase.database.FirebaseDatabase // Imports the FirebaseDatabase class for accessing the Firebase Realtime Database
import java.text.SimpleDateFormat // Imports the SimpleDateFormat class for formatting dates
import java.util.Date // Imports the Date class for representing a specific instant in time
import java.util.Locale // Imports the Locale class for representing a specific geographical, political, or cultural region

class EditNote : AppCompatActivity() { // Defines the EditNote activity, which inherits from AppCompatActivity
    private val binding: ActivityEditNoteBinding by lazy { // Lazily initializes the view binding for the activity
        ActivityEditNoteBinding.inflate(layoutInflater) // Inflates the layout for the activity
    }

    private lateinit var databaseReference: DatabaseReference // Declares a lateinit variable for the Firebase Realtime Database reference
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the Firebase authentication instance

    override fun onCreate(savedInstanceState: Bundle?) { // Overrides the onCreate method, which is called when the activity is first created
        super.onCreate(savedInstanceState) // Calls the superclass's implementation of onCreate
        enableEdgeToEdge() // Enables edge-to-edge display for the activity
        setContentView(binding.root) // Sets the content view of the activity to the root of the binding

        databaseReference = FirebaseDatabase.getInstance().reference // Initializes the database reference to the root of the Firebase Realtime Database
        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance

        val noteId = intent.getStringExtra("noteId") // Gets the note ID from the intent extras
        val noteTitle = intent.getStringExtra("noteTitle") // Gets the note title from the intent extras
        val noteDescription = intent.getStringExtra("noteDescription") // Gets the note description from the intent extras

        binding.editTextTitle.setText(noteTitle) // Sets the text of the title EditText to the note's title
        binding.editTextDescription.setText(noteDescription) // Sets the text of the description EditText to the note's description

        binding.updateNoteButton.setOnClickListener { // Sets an OnClickListener for the update note button
            val newTitle = binding.editTextTitle.text.toString() // Gets the updated text from the title EditText and converts it to a string
            val newDescription = binding.editTextDescription.text.toString() // Gets the updated text from the description EditText and converts it to a string
            val newDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) // Gets the current date and formats it as "dd-MM-yyyy"

            if (noteId != null) { // Checks if the note ID is not null
                updateNoteInDatabase(noteId, newTitle, newDescription, newDate) // Calls the function to update the note in the database
            }
        }
    }

    private fun updateNoteInDatabase(noteId: String, newTitle: String, newDescription: String, newDate: String) { // Defines a function to update the note in the database
        val currentUser = auth.currentUser // Gets the currently signed-in user
        currentUser?.let { user -> // If a user is signed in
            val noteReference = databaseReference.child("Users").child(user.uid).child("Notes").child(noteId) // Gets a reference to the specific note in the database

            val updatedNote = mapOf(
                "title" to newTitle, // Creates a map with the updated title
                "description" to newDescription, // Adds the updated description to the map
                "date" to newDate // Adds the updated date to the map
            )

            noteReference.updateChildren(updatedNote) // Updates the children of the note reference with the new values
                .addOnCompleteListener { task -> // Adds a listener that is called when the operation is complete
                    if (task.isSuccessful) { // If the note was updated successfully
                        Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the note was updated successfully
                        finish() // Finishes the activity
                    } else { // If the note failed to update
                        Toast.makeText(this, "Failed to Update Note", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the note failed to update
                    }
                }
        }
    }
}