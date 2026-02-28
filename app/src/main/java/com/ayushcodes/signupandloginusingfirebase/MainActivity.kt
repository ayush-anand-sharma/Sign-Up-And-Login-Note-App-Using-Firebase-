package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.content.Context // Imports the Context class for accessing application-specific resources and classes
import android.content.Intent // Imports the Intent class for starting new activities
import android.net.ConnectivityManager // Imports the ConnectivityManager class for checking network connectivity
import android.net.NetworkCapabilities // Imports the NetworkCapabilities class for checking network capabilities
import android.os.Bundle // Imports the Bundle class for saving instance state
import android.view.LayoutInflater // Imports the LayoutInflater class to inflate layout XML files
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button // Imports the Button class for creating a button
import android.widget.TextView // Imports the TextView class for displaying text
import android.widget.Toast // Imports the Toast class for showing short messages
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function for enabling edge-to-edge display
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class for compatibility features
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Imports the installSplashScreen function for creating a splash screen
import androidx.recyclerview.widget.LinearLayoutManager // Imports the LinearLayoutManager for arranging items in a vertical or horizontal scrolling list
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityMainBinding // Imports the binding class for the activity_main.xml layout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class for user authentication
import com.google.firebase.database.DataSnapshot // Imports the DataSnapshot class, which is a snapshot of the data at a Firebase Realtime Database location
import com.google.firebase.database.DatabaseError // Imports the DatabaseError class, which is a description of an error that can occur when listening for data
import com.google.firebase.database.DatabaseReference // Imports the DatabaseReference class for interacting with the Firebase Realtime Database
import com.google.firebase.database.FirebaseDatabase // Imports the FirebaseDatabase class for accessing the Firebase Realtime Database
import com.google.firebase.database.ValueEventListener // Imports the ValueEventListener for listening for changes to the data at a Firebase Realtime Database location

class MainActivity : AppCompatActivity(), NoteAdapter.OnitemClickListener { // Defines the MainActivity class, which inherits from AppCompatActivity and implements the OnitemClickListener interface
    private val binding: ActivityMainBinding by lazy { // Lazily initializes the view binding for the activity
        ActivityMainBinding.inflate(layoutInflater) // Inflates the layout for the activity
    }

    private lateinit var databaseReference: DatabaseReference // Declares a lateinit variable for the Firebase Realtime Database reference
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the Firebase authentication instance
    private lateinit var noteAdapter: NoteAdapter // Declares a lateinit variable for the NoteAdapter
    private lateinit var noteReference: DatabaseReference // Declares a lateinit variable for the reference to the user's notes
    private lateinit var valueEventListener: ValueEventListener // Declares a lateinit variable for the listener for note changes

    override fun onCreate(savedInstanceState: Bundle?) { // Overrides the onCreate method, which is called when the activity is first created
        super.onCreate(savedInstanceState) // Calls the superclass's implementation of onCreate
        installSplashScreen() // Installs a splash screen for the activity
        enableEdgeToEdge() // Enables edge-to-edge display for the activity
        setContentView(binding.root) // Sets the content view of the activity to the root of the binding

        databaseReference = FirebaseDatabase.getInstance().reference // Initializes the database reference to the root of the Firebase Realtime Database
        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance

        noteAdapter = NoteAdapter(ArrayList(), this) // Initializes the NoteAdapter with an empty list and the current activity as the click listener
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this) // Sets the layout manager for the RecyclerView
        binding.notesRecyclerView.adapter = noteAdapter // Sets the adapter for the RecyclerView

        showProgressBar(true) // Show progress bar while loading notes

        val currentUser = auth.currentUser // Gets the currently signed-in user
        currentUser?.let { user -> // If a user is signed in
            noteReference = databaseReference.child("Users").child(user.uid).child("Notes") // Gets a reference to the "Notes" node for the current user
            valueEventListener = object : ValueEventListener { // Initializes the value event listener
                override fun onDataChange(snapshot: DataSnapshot) { // Overrides the onDataChange method, which is called when the data changes
                    val noteList = ArrayList<NoteItem>() // Creates a new ArrayList to hold the notes
                    for (noteSnapshot in snapshot.children) { // Iterates over the children of the snapshot
                        val note = noteSnapshot.getValue(NoteItem::class.java) // Gets the note from the snapshot
                        note?.let { // If the note is not null
                            noteList.add(it) // Adds the note to the list
                        }
                    }
                    noteList.sortByDescending { it.isPinned } // Sorts the list to show pinned notes first
                    noteAdapter.updateData(noteList) // Updates the data in the adapter with the new list of notes
                    showProgressBar(false) // Hide progress bar after loading notes
                }

                override fun onCancelled(error: DatabaseError) { // Overrides the onCancelled method, which is called when the listener is cancelled
                    Toast.makeText(this@MainActivity, "Failed to load notes: ${error.message}", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the notes failed to load
                    showProgressBar(false) // Hide progress bar on error
                }
            }
            noteReference.addValueEventListener(valueEventListener) // Adds the listener for changes to the data at the note reference
        }

        binding.addNoteButton.setOnClickListener { // Sets an OnClickListener for the add note button
            if (isNetworkAvailable()) { // Checks if a network connection is available
                startActivity(Intent(this, AddNote::class.java)) // Starts the AddNote activity
            } else { // If no network connection is available
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that there is no internet connection
            }
        }

        binding.menuButton.setOnClickListener { // Sets an OnClickListener for the menu button
            showUserProfileDialog() // Calls the function to show the user profile dialog
        }
    }

    private fun showUserProfileDialog() { // Defines a function to show the user profile dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_profile, null) // Inflates the user profile dialog layout
        val dialog = BottomSheetDialog(this) // Creates a new BottomSheetDialog
        dialog.setContentView(dialogView) // Sets the content view of the dialog

        val currentUser = auth.currentUser // Gets the currently signed-in user
        currentUser?.let { user -> // If a user is signed in
            val userReference = databaseReference.child("Users").child(user.uid) // Gets a reference to the user's data in the database
            userReference.addListenerForSingleValueEvent(object : ValueEventListener { // Adds a listener for a single value event
                override fun onDataChange(snapshot: DataSnapshot) { // Overrides the onDataChange method, which is called when the data changes
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "" // Gets the user's name from the snapshot
                    val userEmail = user.email ?: "" // Gets the user's email

                    dialog.findViewById<TextView>(R.id.userNameTextView)?.text = userName // Sets the text of the user name TextView
                    dialog.findViewById<TextView>(R.id.userEmailTextView)?.text = userEmail // Sets the text of the user email TextView
                }

                override fun onCancelled(error: DatabaseError) { // Overrides the onCancelled method, which is called when the listener is cancelled
                    Toast.makeText(this@MainActivity, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the user data failed to load
                }
            })
        }

        dialog.findViewById<Button>(R.id.dialogSignOutButton)?.setOnClickListener { // Sets an OnClickListener for the sign out button
            AlertDialog.Builder(this) // Creates a new alert dialog builder
                .setTitle("Sign Out") // Sets the title of the dialog
                .setMessage("Are you sure you want to sign out?") // Sets the message of the dialog
                .setPositiveButton("Sign Out") { _, _ -> // Sets the positive button and its click listener
                    if (isNetworkAvailable()) { // Checks if a network connection is available
                        showProgressBar(true) // Show progress bar before signing out.
                        if (::noteReference.isInitialized && ::valueEventListener.isInitialized) { // Checks if the note reference and value event listener are initialized
                            noteReference.removeEventListener(valueEventListener) // Removes the value event listener from the note reference
                        }
                        auth.signOut() // Signs out the current user
                        dialog.dismiss() // Dismisses the dialog
                        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the user is signing out
                        startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity
                        finish() // Finishes the current activity
                    } else { // If no network connection is available
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that there is no internet connection
                    }
                }
                .setNegativeButton("Cancel", null) // Sets the negative button and its click listener
                .show() // Shows the alert dialog
        }

        dialog.show() // Shows the dialog
    }

    override fun onEditClick(noteID: String) { // Overrides the onEditClick method from the OnitemClickListener interface
        val intent = Intent(this, EditNote::class.java) // Creates an Intent to start the EditNote
        val note = noteAdapter.getNote(noteID) // Retrieves the note from the adapter using its ID.
        intent.putExtra("noteId", noteID) // Puts the noteID as an extra in the Intent
        intent.putExtra("noteTitle", note?.title) // Puts the noteTitle as an extra in the Intent, making it available for the EditNote activity.
        intent.putExtra("noteDescription", note?.description) // Puts the noteDescription as an extra in the Intent, making it available for the EditNote activity.
        startActivity(intent) // Starts the EditNote
    }

    override fun onDeleteClick(noteID: String) { // Overrides the onDeleteClick method from the OnitemClickListener interface
        val currentUser = auth.currentUser // Gets the currently signed-in user
        currentUser?.let { user -> // If a user is signed in
            val noteReference = databaseReference.child("Users").child(user.uid).child("Notes").child(noteID) // Gets a reference to the specific note to be deleted
            noteReference.removeValue().addOnCompleteListener { task -> // Removes the value from the database
                if (task.isSuccessful) { // If the operation is successful
                    Toast.makeText(this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the note was deleted successfully
                } else { // If the operation fails
                    Toast.makeText(this, "Failed to Delete Note: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the note failed to delete
                }
            }
        }
    }

    override fun onPinClick(noteID: String, isPinned: Boolean) { // Overrides the onPinClick method from the OnitemClickListener interface
        val currentUser = auth.currentUser // Gets the currently signed-in user
        currentUser?.let { user -> // If a user is signed in
            val noteReference = databaseReference.child("Users").child(user.uid).child("Notes").child(noteID).child("isPinned") // Gets a reference to the isPinned property of the specific note
            noteReference.setValue(isPinned).addOnCompleteListener { task -> // Sets the value of the isPinned property in the database
                if (task.isSuccessful) { // If the operation is successful
                    Toast.makeText(this, if (isPinned) "Note Pinned" else "Note Unpinned", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the note was pinned or unpinned
                } else { // If the operation fails
                    Toast.makeText(this, "Failed to update pin status", Toast.LENGTH_SHORT).show() // Shows a toast message indicating that the pin status failed to update
                }
            }
        }
    }

    override fun onNoteClick(note: NoteItem) { // Overrides the onNoteClick method from the OnitemClickListener interface
        val intent = Intent(this, ReadNote::class.java) // Creates an Intent to start the ReadNote activity
        intent.putExtra("noteTitle", note.title) // Puts the note title as an extra in the Intent
        intent.putExtra("noteDescription", note.description) // Puts the note description as an extra in the Intent
        intent.putExtra("noteDate", note.date) // Puts the note date as an extra in the Intent
        startActivity(intent) // Starts the ReadNote activity
    }

    // This function shows or hides the progress bar and dim overlay.
    private fun showProgressBar(show: Boolean) { // Defines a function to show or hide the progress bar.
        if (show) { // If the `show` parameter is true,
            binding.progressBar.visibility = View.VISIBLE // Makes the progress bar visible.
            binding.dimOverlay.visibility = View.VISIBLE // Makes the dim overlay visible.
            binding.addNoteButton.isEnabled = false // Disables the add note button to prevent user interaction.
            hideKeyboard() // Hides the keyboard.
        } else { // If the `show` parameter is false,
            binding.progressBar.visibility = View.GONE // Hides the progress bar.
            binding.dimOverlay.visibility = View.GONE // Hides the dim overlay.
            binding.addNoteButton.isEnabled = true // Enables the add note button again.
        }
    }

    // This function hides the soft keyboard.
    private fun hideKeyboard() { // Defines a function to hide the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // Gets an instance of the InputMethodManager.
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) // Hides the keyboard from the currently focused window.
    }

    private fun isNetworkAvailable(): Boolean { // Defines a function to check for an active internet connection
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets an instance of the ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false // Gets the currently active network, or returns false if there is none
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false // Gets the capabilities of the active network, or returns false if they cannot be determined
        return when { // Returns true if the network has any of the following transport types
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // The network is a Wi-Fi network
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // The network is a cellular network
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true // The network is an Ethernet network
            else -> false // The network does not have any of the supported transport types
        }
    }
}