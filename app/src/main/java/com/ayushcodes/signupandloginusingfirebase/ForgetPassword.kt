package com.ayushcodes.signupandloginusingfirebase

import android.content.Intent // Imports the Intent class, used to start new activities.
import android.os.Bundle // Imports the Bundle class, used for passing data between activities.
import android.util.Patterns // Imports the Patterns class, which contains pre-defined validation patterns.
import android.widget.Toast // Imports the Toast class, used to display short notifications to the user.
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function, which enables edge-to-edge display.
import androidx.appcompat.app.AlertDialog // Imports the AlertDialog class, used to show a dialog message.
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class, a base class for activities that use the support library action bar features.
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityForgetPasswordBinding // Imports the generated binding class for the activity_forget_password.xml layout.
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class, the entry point of the Firebase Authentication SDK.

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
            val email = binding.emailtext.text.toString() // Gets the email from the text field.

            if (email.isEmpty()) { // If the email field is empty,
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the email format is invalid,
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else { // Otherwise,
                auth.fetchSignInMethodsForEmail(email) // Fetches the sign-in methods for the given email.
                    .addOnCompleteListener { task -> // Adds a listener that is called when the task completes.
                        if (task.isSuccessful) { // If the fetch is successful,
                            val isNewUser = task.result?.signInMethods?.isEmpty() ?: true // Checks if the user is new.
                            if (isNewUser) { // If the user is new,
                                Toast.makeText(this, "This email is not registered on this application", Toast.LENGTH_SHORT).show() // shows a toast message.
                            } else { // Otherwise,
                                AlertDialog.Builder(this) // Creates a new alert dialog builder.
                                    .setTitle("Confirm Password Reset") // Sets the title of the dialog.
                                    .setMessage("Are you sure you want to send a password reset link to this email?") // Sets the message of the dialog.
                                    .setPositiveButton("Confirm") { _, _ -> // Sets the positive button and its click listener.
                                        auth.sendPasswordResetEmail(email) // Sends a password reset email to the given email address.
                                            .addOnCompleteListener { task -> // Adds a listener that is called when the task completes.
                                                if (task.isSuccessful) { // If the email is sent successfully,
                                                    Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show() // shows a success message,
                                                    startActivity(Intent(this, SignInPage::class.java)) // starts the SignInPage,
                                                    finish() // and finishes the current activity.
                                                } else { // If the email sending fails,
                                                    Toast.makeText(this, "Failed to send reset link: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // shows an error message.
                                                }
                                            }
                                    }
                                    .setNegativeButton("Cancel") { dialog, _ -> // Sets the negative button and its click listener.
                                        dialog.dismiss() // Dismisses the dialog.
                                    }
                                    .create() // Creates the alert dialog.
                                    .show() // Shows the alert dialog.
                            }
                        } else { // If the fetch fails,
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // shows an error message.
                        }
                    }
            }
        }

        binding.registerText.setOnClickListener { // Sets a click listener on the register text.
            startActivity(Intent(this, SignUpPage::class.java)) // Starts the SignUpPage activity.
            finish() // Finishes the current activity.
        }
    }
}
