package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class.

import android.content.Context // Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Intent // Imports the Intent class, used to start new activities.
import android.net.ConnectivityManager // Imports the ConnectivityManager class, which provides information about network connectivity.
import android.net.NetworkCapabilities // Imports the NetworkCapabilities class, which describes the properties of a network.
import android.os.Bundle // Imports the Bundle class, used for passing data between activities.
import android.util.Patterns // Imports the Patterns class, which contains pre-defined validation patterns.
import android.view.View // Imports the View class, the basic building block for user interface components.
import android.view.inputmethod.InputMethodManager // Imports the InputMethodManager class, for interacting with the input method framework (soft keyboard).
import android.widget.Toast // Imports the Toast class, used to display short notifications to the user.
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function, which enables edge-to-edge display.
import androidx.appcompat.app.AlertDialog // Imports the AlertDialog class, used to show a dialog message.
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class, a base class for activities that use the support library action bar features.
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityForgetPasswordBinding // Imports the generated binding class for the activity_forget_password.xml layout.
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class, the entry point of the Firebase Authentication SDK.
import com.google.firebase.database.DataSnapshot // Imports DataSnapshot for reading data from the Firebase Realtime Database.
import com.google.firebase.database.DatabaseError // Imports DatabaseError for handling errors during database operations.
import com.google.firebase.database.FirebaseDatabase // Imports FirebaseDatabase for accessing the Realtime Database.
import com.google.firebase.database.ValueEventListener // Imports ValueEventListener for listening to data changes.

class ForgetPassword : AppCompatActivity() { // Defines the ForgetPassword class, which inherits from AppCompatActivity.
    private val binding: ActivityForgetPasswordBinding by lazy { // Lazily initializes the view binding for the activity.
        ActivityForgetPasswordBinding.inflate(layoutInflater) // Inflates the layout for this activity.
    }
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the FirebaseAuth instance.

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is first created.
        super.onCreate(savedInstanceState) // Calls the superclass implementation.
        enableEdgeToEdge() // Enables edge-to-edge display.
        setContentView(binding.root) // Sets the content view to the root of the binding.

        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance.

        binding.backButton.setOnClickListener { // Sets a click listener on the back button.
            startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity.
            finish() // Finishes the current activity.
        }

        binding.sendLinkButton.setOnClickListener { // Sets a click listener on the send link button.
            if (!isNetworkAvailable()) { // Checks for network connectivity.
                Toast.makeText(this, "Network Connection Error.", Toast.LENGTH_SHORT).show() // Shows a toast if no connection.
                return@setOnClickListener // Returns from the listener.
            }
            val email = binding.emailtext.text.toString().trim() // Gets the email from the text field and trims whitespace.

            if (email.isEmpty()) { // If the email field is empty,
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the email format is invalid,
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else { // Otherwise, // Show the confirmation dialog directly without showing the progress bar first.
                showPasswordResetDialog(email)
            }
        }

        binding.registerText.setOnClickListener { // Sets a click listener on the register text.
            startActivity(Intent(this, SignUpPage::class.java)) // Starts the SignUpPage activity.
            finish() // Finishes the current activity.
        }
    }

    // This function displays a confirmation dialog. When confirmed, it checks for the email and sends the reset link.
    private fun showPasswordResetDialog(email: String) {
        AlertDialog.Builder(this) // Creates a new alert dialog builder.
            .setTitle("Confirm Password Reset") // Sets the title of the dialog.
            .setMessage("Are you sure you want to send a password reset link to this email?") // Sets the message of the dialog.
            .setPositiveButton("Confirm") { dialog, _ -> // Sets the positive button ("Confirm") and its click listener.
                // When the user confirms, show the progress bar and start the process.
                showProgressBar(true)

                // Check if the email is registered in Firebase Authentication.
                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) { // If the check is successful,
                        val signInMethods = task.result?.signInMethods // get the list of sign-in methods.
                        if (signInMethods.isNullOrEmpty()) { // If the list is null or empty, the email is not registered in Firebase Auth.
                            // Email does not exist in Firebase Auth, so check Realtime Database as a fallback.
                            val usersRef = FirebaseDatabase.getInstance().getReference("Users") // Get a reference to the "Users" node in the database.
                            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener { // Query the database for a user with the matching email.
                                override fun onDataChange(snapshot: DataSnapshot) { // This method is called when the data is retrieved.
                                    if (snapshot.exists()) { // If a user with the email exists in the Realtime Database,
                                        // Email exists in Realtime Database, but not in Auth.
                                        // Proceed with sending the password reset link.
                                        sendResetLink(email)
                                    } else { // If the email doesn't exist in the database either,
                                        // Email does not exist in either Firebase Auth or Realtime Database.
                                        showProgressBar(false) // hide the progress bar,
                                        Toast.makeText(this@ForgetPassword, "This email is not registered on this application", Toast.LENGTH_SHORT).show() // and show a toast message.
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) { // This method is called if the database operation is cancelled or fails.
                                    showProgressBar(false) // Hide the progress bar,
                                    Toast.makeText(this@ForgetPassword, "Database error: ${error.message}", Toast.LENGTH_SHORT).show() // and show an error message.
                                }
                            })
                        } else { // If the email exists in Firebase Auth,
                            // Email exists in Firebase Auth, so we can proceed with the password reset.
                            sendResetLink(email)
                        }
                    } else { // If fetching sign-in methods fails,
                        showProgressBar(false) // hide the progress bar,
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // and show an error message.
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> // Sets the negative button ("Cancel") and its click listener.
                dialog.dismiss() // Dismisses the dialog.
            }
            .create() // Creates the alert dialog.
            .show() // Shows the alert dialog.
    }
    
    // Helper function to send the password reset email.
    private fun sendResetLink(email: String) {
        auth.sendPasswordResetEmail(email) // Sends a password reset email to the given email address.
            .addOnCompleteListener { sendTask -> // Adds a listener that is called when the task completes.
                showProgressBar(false) // Hides the progress bar.

                if (sendTask.isSuccessful) { // If the email is sent successfully,
                    Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show() // shows a success message,
                    startActivity(Intent(this, SignInPage::class.java)) // starts the SignInPage,
                    finish() // and finishes the current activity.
                } else { // If the email sending fails,
                    Toast.makeText(this, "Failed to send reset link: ${sendTask.exception?.message}", Toast.LENGTH_SHORT).show() // shows an error message.
                }
            }
    }


    // This function shows or hides the progress bar and a dim overlay.
    private fun showProgressBar(show: Boolean) {
        if (show) { // If 'show' is true:
            binding.progressBar.visibility = View.VISIBLE // Make the progress bar visible.
            binding.dimOverlay.visibility = View.VISIBLE // Make the dim overlay visible.
            binding.sendLinkButton.isEnabled = false // Disable the "Send Link" button to prevent multiple clicks.
            binding.registerText.isEnabled = false // Disable the "Register" text view.
            hideKeyboard() // Hide the on-screen keyboard.
        } else { // If 'show' is false:
            binding.progressBar.visibility = View.GONE // Hide the progress bar.
            binding.dimOverlay.visibility = View.GONE // Hide the dim overlay.
            binding.sendLinkButton.isEnabled = true // Re-enable the "Send Link" button.
            binding.registerText.isEnabled = true // Re-enable the "Register" text view.
        }
    }

    // This function hides the soft keyboard.
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // Get an instance of InputMethodManager.
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) // Hide the keyboard from the currently focused window.
    }

    // Checks if a network connection is available.
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets the ConnectivityManager system service.
        val network = connectivityManager.activeNetwork ?: return false // Gets the active network, or returns false if there is none.
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false // Gets the network capabilities, or returns false if they are unavailable.
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Returns true if the network has an internet connection, false otherwise.
    }
}
