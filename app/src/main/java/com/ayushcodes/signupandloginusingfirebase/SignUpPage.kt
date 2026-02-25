package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class.

import android.content.Context // Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Intent // Imports the Intent class, used to start new activities.
import android.net.ConnectivityManager // Imports the ConnectivityManager class, which provides information about network connectivity.
import android.net.NetworkCapabilities // Imports the NetworkCapabilities class, which describes the properties of a network.
import android.os.Bundle // Imports the Bundle class, used for passing data between activities.
import android.text.InputType // Imports the InputType class, used for defining the type of content in an EditText.
import android.util.Patterns // Imports the Patterns class, which contains standard regular expressions.
import android.widget.EditText // Imports the EditText class, a user interface element for entering and modifying text.
import android.widget.ImageView // Imports the ImageView class, a user interface element for displaying images.
import android.widget.Toast // Imports the Toast class, used to display short messages to the user.
import androidx.activity.OnBackPressedCallback // Imports the OnBackPressedCallback class, for handling back button presses.
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function, to enable display of content from edge to edge.
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class, a base class for activities that use the support library action bar features.
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Imports the installSplashScreen method to display a splash screen.
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivitySignUpPageBinding // Imports the binding class for the sign-up page layout.
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class, for handling user authentication.
import com.google.firebase.auth.FirebaseAuthUserCollisionException // Imports the exception for when a user tries to register with an existing email.
import com.google.firebase.database.FirebaseDatabase // Imports the FirebaseDatabase class, for accessing the Firebase Realtime Database.

class SignUpPage : AppCompatActivity() { // Defines the SignUpPage class, which inherits from AppCompatActivity.
    // Lazily initializes the binding object for the activity's layout.
    private val binding: ActivitySignUpPageBinding by lazy {
        ActivitySignUpPageBinding.inflate(layoutInflater) // Inflates the layout for the sign-up page.
    }
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the FirebaseAuth instance.
    private lateinit var database: FirebaseDatabase // Declares a lateinit variable for the FirebaseDatabase instance.

    override fun onCreate(savedInstanceState: Bundle?) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState) // Calls the superclass's onCreate method.
        installSplashScreen() // Installs the splash screen.
        enableEdgeToEdge() // Enables edge-to-edge display for the activity.
        setContentView(binding.root) // Sets the content view of the activity to the root of the binding.

        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance.
        database = FirebaseDatabase.getInstance() // Initializes the FirebaseDatabase instance.

        // Sets a click listener for the back button.
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity.
            finish() // Finishes the current activity.
        }

        // Sets a click listener for the register button.
        binding.RegisterButton.setOnClickListener {
            // Checks if a network connection is available.
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Network Connection Error.", Toast.LENGTH_SHORT).show() // Shows a toast message if there is no network connection.
                return@setOnClickListener // Exits the click listener.
            }
            val email = binding.emailtext.text.toString() // Gets the email from the EditText.
            val name = binding.nameText.text.toString() // Gets the name from the EditText.
            val password = binding.passwordText.text.toString() // Gets the password from the EditText.
            val repeatPassword = binding.repeatPasswordText.text.toString() // Gets the repeated password from the EditText.

            // Checks if any of the fields are empty.
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show() // Shows a toast message if any field is empty.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Checks if the email is valid.
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show() // Shows a toast message if the email is invalid.
            } else if (password != repeatPassword) { // Checks if the passwords match.
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show() // Shows a toast message if the passwords do not match.
            } else if (!isPasswordStrong(password)) { // Checks if the password is strong enough.
                Toast.makeText(this, "Password must be at least 8 characters long and contain only letters and numbers.", Toast.LENGTH_LONG).show() // Shows a toast message if the password is not strong enough.
            } else {
                // Creates a new user with the given email and password.
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task -> // Adds a completion listener to the task.
                        if (task.isSuccessful) { // Checks if the task was successful.
                            val user = auth.currentUser // Gets the current user.
                            // Saves user data in the Realtime Database.
                            val userReference = database.reference.child("Users").child(user!!.uid) // Gets a reference to the user's data in the database.
                            val userData = hashMapOf("email" to email, "name" to name) // Creates a map of user data.
                            userReference.setValue(userData) // Sets the user's data in the database.
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show() // Shows a toast message indicating successful registration.
                            startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity.
                            finish() // Finishes the current activity.
                        } else {
                            // Handles registration failure.
                            when (task.exception) { // Checks the type of exception that occurred.
                                is FirebaseAuthUserCollisionException -> Toast.makeText(this, "This email is already registered on this application.", Toast.LENGTH_SHORT).show() // Shows a toast message if the email is already in use.
                                else -> Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // Shows a generic registration failure message.
                            }
                        }
                    }
            }
        }

        // Sets a click listener for the sign-in button.
        binding.signInButton.setOnClickListener {
            startActivity(Intent(this, SignInPage::class.java)) // Starts the SignInPage activity.
            finish() // Finishes the current activity.
        }

        // Sets a click listener for the show password button.
        binding.showPassword.setOnClickListener {
            togglePasswordVisibility(binding.passwordText, binding.showPassword) // Toggles the visibility of the password.
        }

        // Sets a click listener for the show repeat password button.
        binding.showRepeatPassword.setOnClickListener {
            togglePasswordVisibility(binding.repeatPasswordText, binding.showRepeatPassword) // Toggles the visibility of the repeated password.
        }

        // Handles back press to navigate to SignInPage.
        val callback = object : OnBackPressedCallback(true) { // Creates a new OnBackPressedCallback.
            override fun handleOnBackPressed() { // Overrides the handleOnBackPressed method.
                startActivity(Intent(this@SignUpPage, SignInPage::class.java)) // Starts the SignInPage activity.
                finish() // Finishes the current activity.
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // Adds the callback to the OnBackPressedDispatcher.
    }

    // Checks if the password is strong.
    private fun isPasswordStrong(password: String): Boolean {
        // Password must be at least 8 characters long and contain only letters and numbers.
        val passwordPattern = "^[a-zA-Z0-9]{8,}$".toRegex() // Defines the regular expression for a strong password.
        return password.matches(passwordPattern) // Returns true if the password matches the pattern, false otherwise.
    }

    // Toggles the visibility of the password in an EditText.
    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) {
        val isPasswordVisible = editText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Checks if the password is currently visible.
        if (isPasswordVisible) { // If the password is not visible...
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // ...makes the password visible.
            imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel) // Changes the image to a 'hide' icon.
        } else { // If the password is a visible...
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // ...hides the password.
            imageView.setImageResource(android.R.drawable.ic_menu_view) // Changes the image to a 'show' icon.
        }
        editText.setSelection(editText.text.length) // Moves the cursor to the end of the text.
    }

    // Checks if a network connection is available.
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Gets the ConnectivityManager system service.
        val network = connectivityManager.activeNetwork ?: return false // Gets the active network, or returns false if there is none.
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false // Gets the network capabilities, or returns false if they are unavailable.
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Returns true if the network has an internet connection, false otherwise.
    }
}
