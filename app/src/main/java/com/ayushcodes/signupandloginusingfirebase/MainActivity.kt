package com.ayushcodes.signupandloginusingfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityMainBinding

import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val  binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        binding.createNoteButton.setOnClickListener {
            startActivity(Intent(this, AddNote::class.java))
        }
        binding.signOutButton.setOnClickListener{
            auth.signOut()
            Toast.makeText(this,"Signing Out...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignInPage::class.java))
            finish()
        }
    }
}