package com.ayushcodes.signupandloginusingfirebase

import android.content.Context // Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Intent // Imports the Intent class, used to start new activities.
import android.net.ConnectivityManager // Imports the ConnectivityManager class, which provides information about the state of network connectivity.
import android.net.NetworkCapabilities // Imports NetworkCapabilities to check for network transport types.
import android.os.Bundle // Imports the Bundle class, used for passing data between activities.
import android.text.InputType // Imports the InputType class, used to specify the type of content in a text field.
import android.util.Patterns // Imports the Patterns class, which contains pre-defined validation patterns.
import android.widget.Toast // Imports the Toast class, used to display short notifications to the user.
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function, which enables edge-to-edge display.
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class, a base class for activities that use the support library action bar features.
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Imports the installSplashScreen function, used to show a splash screen.
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivitySignInPageBinding // Imports the generated binding class for the activity_sign_in_page.xml layout.
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class, the entry point of the Firebase Authentication SDK.
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException // Imports the exception for invalid credentials.
import com.google.firebase.auth.FirebaseAuthInvalidUserException // Imports the exception for an invalid user.
import com.google.firebase.auth.FirebaseUser // Imports the FirebaseUser class, which represents a user's profile information in your project's user database.

class SignInPage : AppCompatActivity() { // Defines the SignInPage class, which inherits from AppCompatActivity.
    private val binding: ActivitySignInPageBinding by lazy { // Lazily initializes the view binding for the activity.
        ActivitySignInPageBinding.inflate(layoutInflater) // Inflates the layout for this activity.
    }
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the FirebaseAuth instance.

    override fun onStart() { // Called when the activity is becoming visible to the user.
        super.onStart() // Calls the superclass implementation.

        val currentUser: FirebaseUser? = auth.currentUser // Gets the currently signed-in user.
        if (currentUser != null) { // If a user is already signed in,
            startActivity(Intent(this, MainActivity::class.java)) // starts the MainActivity,
            finish() // and finishes the current activity.
            return // Exits the onStart method.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is first created.
        super.onCreate(savedInstanceState) // Calls the superclass implementation.
        installSplashScreen() // Installs the splash screen.
        enableEdgeToEdge() // Enables edge-to-edge display.
        setContentView(binding.root) // Sets the content view to the root of the binding.

        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance.

        binding.forgotPassword.setOnClickListener { // Sets a click listener on the forgot password text view.
            startActivity(Intent(this, ForgetPassword::class.java)) // Starts the ForgetPassword activity.
        }

        binding.logInButton.setOnClickListener { // Sets a click listener on the login button.
            if (!isNetworkAvailable()) { // Checks if the network is available.
                Toast.makeText(this, "Network Connection Error.", Toast.LENGTH_SHORT).show() // Shows a toast message if the network is not available.
                return@setOnClickListener // Exits the click listener.
            }
            val email = binding.usernameText.text.toString() // Gets the username from the text field.
            val password = binding.passwordText.text.toString() // Gets the password from the text field.

            if (email.isEmpty() || password.isEmpty()) { // If the username or password fields are empty,
                Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the email format is invalid,
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else { // Otherwise,
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val isNewUser = task.result?.signInMethods?.isEmpty() ?: true
                            if (isNewUser) {
                                Toast.makeText(this, "This email is not registered on this application", Toast.LENGTH_SHORT).show()
                            } else {
                                auth.signInWithEmailAndPassword(email, password) // signs in the user with their email and password.
                                    .addOnCompleteListener(this) { signInTask -> // Adds a listener that is called when the task completes.
                                        if (signInTask.isSuccessful) { // If the sign-in is successful,
                                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show() // shows a success message,
                                            startActivity(Intent(this, MainActivity::class.java)) // starts the MainActivity,
                                            finish() // and finishes the current activity.
                                        } else { // If the sign-in fails,
                                            val exception = signInTask.exception // Gets the exception from the task.
                                            val errorMessage = when (exception) { // Determines the error message based on the exception type.
                                                is FirebaseAuthInvalidCredentialsException -> "Your email or password is incorrect."
                                                else -> "Login Failed: ${exception?.message}"
                                            }
                                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show() // shows an error message.
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.signUpButton.setOnClickListener { // Sets a click listener on the sign-up button.
            if (!isNetworkAvailable()) { // Checks if the network is available.
                Toast.makeText(this, "Network Connection Error.", Toast.LENGTH_SHORT).show() // Shows a toast message if the network is not available.
                return@setOnClickListener // Exits the click listener.
            }
            startActivity(Intent(this, SignUpPage::class.java)) // Starts the SignUpPage activity.
            finish() // Finishes the current activity.
        }

        binding.showPassword.setOnClickListener { // Sets a click listener on the show password icon.
            val passwordEditText = binding.passwordText // Gets the password EditText.
            val isPasswordVisible = passwordEditText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Checks if the password is currently visible.
            if (isPasswordVisible) { // If the password is not visible,
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // makes it visible,
                binding.showPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel) // and changes the icon to the 'hide' icon.
            } else { // If the password is a visible,
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // hides it,
                binding.showPassword.setImageResource(android.R.drawable.ic_menu_view) // and changes the icon back to the 'show' icon.
            }
            passwordEditText.setSelection(passwordEditText.text.length) // Moves the cursor to the end of the text.
        }

        val callback = object : OnBackPressedCallback(true) { // Creates a callback for handling the back button press.
            override fun handleOnBackPressed() { // This method is called when the back button is pressed.
                AlertDialog.Builder(this@SignInPage) // Creates a new alert dialog builder.
                    .setTitle("Exit Application") // Sets the title of the dialog.
                    .setMessage("Are you sure you want to close the application?") // Sets the message of the dialog.
                    .setPositiveButton("Confirm") { _, _ -> // Sets the positive button and its click listener.
                        finishAffinity() // Finishes this activity and all activities immediately below it in the current task.
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> // Sets the negative button and its click listener.
                        dialog.dismiss() // Dismisses the dialog.
                    }
                    .create() // Creates the alert dialog.
                    .show() // Shows the alert dialog.
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // Adds the callback to the OnBackPressedDispatcher.
    }

    private fun isNetworkAvailable(): Boolean { // A function to check if the network is available.
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets the ConnectivityManager system service.
        val network = connectivityManager.activeNetwork ?: return false // gets the active network.
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false // gets the network capabilities.
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Returns true if the network has an internet connection.
    }
}
