package com.ayushcodes.signupandloginusingfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivitySignUpPageBinding

import com.google.firebase.auth.FirebaseAuth

class SignUpPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private  val binding : ActivitySignUpPageBinding by lazy {
        ActivitySignUpPageBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            startActivity(Intent(this, SignInPage::class.java))
            finish()
        }
        binding.RegisterButton.setOnClickListener {
            val email = binding.emailtext.text.toString()
            val username = binding.usernameText.text.toString()
            val password = binding.passwordText.text.toString()
            val repeatPassword = binding.repeatPasswordText.text.toString()

            if(email.isEmpty()||username.isEmpty()||password.isEmpty()||repeatPassword.isEmpty())
            {
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }
            else if (password!=repeatPassword)
            {
                Toast.makeText(this,"Repeat Password must be same as Password",Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SignInPage::class.java))
                            finish()
                        } else {
                            Toast.makeText(this,"Registration Failed : ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}