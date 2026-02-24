package com.ayushcodes.signupandloginusingfirebase

import android.content.Context // Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Intent // Imports the Intent class, used to start new activities.
import android.net.ConnectivityManager // Imports the ConnectivityManager class, which provides information about the state of network connectivity.
import android.net.NetworkCapabilities // Imports NetworkCapabilities to check for network transport types.
import android.os.Bundle // Imports the Bundle class, used for passing data between activities.
import android.text.InputType // Imports the InputType class, used to specify the type of content in a text field.
import android.util.Patterns // Imports the Patterns class, which contains pre-defined validation patterns.
import android.widget.EditText // Imports the EditText class, a user interface element for entering and modifying text.
import android.widget.ImageView // Imports the ImageView class, which displays image resources.
import android.widget.Toast // Imports the Toast class, used to display short notifications to the user.
import androidx.activity.OnBackPressedCallback // Imports the OnBackPressedCallback class, used for handling back button presses.
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function, which enables edge-to-edge display.
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class, a base class for activities that use the support library action bar features.
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Imports the installSplashScreen function, used to show a splash screen.
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivitySignUpPageBinding // Imports the generated binding class for the activity_sign_up_page.xml layout.
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class, the entry point of the Firebase Authentication SDK.
import com.google.firebase.auth.FirebaseAuthUserCollisionException // Imports the exception for when a user with the same email already exists.
import com.google.firebase.database.FirebaseDatabase // Imports the FirebaseDatabase class, the entry point for accessing a Firebase Database.

class SignUpPage : AppCompatActivity() { // Defines the SignUpPage class, which inherits from AppCompatActivity.
    private val binding: ActivitySignUpPageBinding by lazy { // Lazily initializes the view binding for the activity.
        ActivitySignUpPageBinding.inflate(layoutInflater) // Inflates the layout for this activity.
    }
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the FirebaseAuth instance.
    private lateinit var database: FirebaseDatabase // Declares a lateinit variable for the FirebaseDatabase instance.

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is first created.
        super.onCreate(savedInstanceState) // Calls the superclass implementation.
        installSplashScreen() // Installs the splash screen.
        enableEdgeToEdge() // Enables edge-to-edge display.
        setContentView(binding.root) // Sets the content view to the root of the binding.

        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance.
        database = FirebaseDatabase.getInstance() // Initializes the FirebaseDatabase instance.

        binding.backButton.setOnClickListener { // Handles the click event for the back button.
            startActivity(Intent(this, SignInPage::class.java)) // Navigates the user to the SignInPage.
            finish() // Finishes the current activity.
        }

        binding.RegisterButton.setOnClickListener { // Sets a click listener on the register button.
            if (!isNetworkAvailable()) { // Checks if the network is available.
                Toast.makeText(this, "Network Connection Error.", Toast.LENGTH_SHORT).show() // Shows a toast message if the network is not available.
                return@setOnClickListener // Exits the click listener.
            }
            val email = binding.emailtext.text.toString() // Gets the email from the text field.
            val name = binding.nameText.text.toString() // Gets the name from the text field.
            val password = binding.passwordText.text.toString() // Gets the password from the text field.
            val repeatPassword = binding.repeatPasswordText.text.toString() // Gets the repeated password from the text field.

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) { // If any of the fields are empty,
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the email format is invalid,
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (password != repeatPassword) { // If the passwords do not match,
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show() // shows a toast message.
            } else if (!isPasswordStrong(password)) { // If the password is not strong enough,
                Toast.makeText(this, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show() // shows a toast message.
            } else { // Otherwise,
                auth.createUserWithEmailAndPassword(email, password) // creates a new user with the given email and password.
                    .addOnCompleteListener(this) { task -> // Adds a listener that is called when the task completes.
                        if (task.isSuccessful) { // If the user creation is successful,
                            val user = auth.currentUser // Gets the current user.
                            val userReference = database.reference.child("Users").child(user!!.uid) // Gets a reference to the user's data in the database.
                            val userData = hashMapOf("email" to email, "name" to name) // Creates a map of the user's data.
                            userReference.setValue(userData) // Sets the user's data in the database.
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show() // shows a success message,
                            startActivity(Intent(this, SignInPage::class.java)) // starts the SignInPage,
                            finish() // and finishes the current activity.
                        } else { // If the user creation fails,
                            when (task.exception) { // Checks the type of exception.
                                is FirebaseAuthUserCollisionException -> Toast.makeText(this, "This email is already registered on this application.", Toast.LENGTH_SHORT).show() // Shows a toast message if the email is already in use.
                                else -> Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // shows an error message for other failures.
                            }
                        }
                    }
            }
        }

        binding.signInButton.setOnClickListener { // Sets a click listener on the sign-in button.
            startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity.
            finish() // Finishes the current activity.
        }

        binding.showPassword.setOnClickListener { // Sets a click listener on the show password icon.
            togglePasswordVisibility(binding.passwordText, binding.showPassword) // Toggles the password visibility.
        }

        binding.showRepeatPassword.setOnClickListener { // Sets a click listener on the show repeat password icon.
            togglePasswordVisibility(binding.repeatPasswordText, binding.showRepeatPassword) // Toggles the repeat password visibility.
        }

        val callback = object : OnBackPressedCallback(true) { // Creates a callback for handling the back button press.
            override fun handleOnBackPressed() { // This method is called when the back button is pressed.
                startActivity(Intent(this@SignUpPage, SignInPage::class.java)) // Starts the SignInPage activity.
                finish() // Finishes the current activity.
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // Adds the callback to the OnBackPressedDispatcher.
    }

    private fun isPasswordStrong(password: String): Boolean { // Checks if the password meets the strength requirements.
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$".toRegex() // Defines the regex for a strong password.
        return password.matches(passwordPattern) // Returns true if the password matches the pattern.
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) { // A function to toggle the password visibility.
        val isPasswordVisible = editText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Checks if the password is currently visible.
        if (isPasswordVisible) { // If the password is not visible,
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // makes it visible,
            imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel) // and changes the icon to the 'hide' icon.
        } else { // If the password is visible,
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // hides it,
            imageView.setImageResource(android.R.drawable.ic_menu_view) // and changes the icon back to the 'show' icon.
        }
        editText.setSelection(editText.text.length) // Moves the cursor to the end of the text.
    }

    private fun isNetworkAvailable(): Boolean { // A function to check if the network is available.
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets the ConnectivityManager system service.
        val network = connectivityManager.activeNetwork ?: return false // gets the active network.
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false // gets the network capabilities.
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Returns true if the network has an internet connection.
    }
}
