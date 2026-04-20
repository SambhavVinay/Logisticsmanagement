package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name            = findViewById<EditText>(R.id.name)
        val email           = findViewById<EditText>(R.id.email)
        val password        = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn     = findViewById<Button>(R.id.registerBtn)
        val progressBar     = findViewById<ProgressBar>(R.id.signupProgress)
        val auth            = FirebaseAuth.getInstance()

        registerBtn.setOnClickListener {
            val nameText    = name.text.toString().trim()
            val emailText   = email.text.toString().trim()
            val passText    = password.text.toString().trim()
            val confirmText = confirmPassword.text.toString().trim()

            if (nameText.length < 3)        { name.error = "Name must be at least 3 characters"; return@setOnClickListener }
            if (emailText.isEmpty())        { email.error = "Enter email"; return@setOnClickListener }
            if (passText.length < 6)        { password.error = "Password must be at least 6 characters"; return@setOnClickListener }
            if (passText != confirmText)    { confirmPassword.error = "Passwords do not match"; return@setOnClickListener }

            progressBar.visibility = View.VISIBLE
            registerBtn.isEnabled = false

            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    registerBtn.isEnabled = true

                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(nameText)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                progressBar.visibility = View.GONE
                                registerBtn.isEnabled = true
                                
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(this@SignupActivity, "Account created!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val error = profileTask.exception?.localizedMessage ?: "Failed to set display name"
                                    Toast.makeText(this@SignupActivity, "Account created, but profile update failed: $error", Toast.LENGTH_LONG).show()
                                    // Still navigate since account IS created
                                    startActivity(Intent(this@SignupActivity, DashboardActivity::class.java))
                                    finish()
                                }
                            }
                    } else {
                        progressBar.visibility = View.GONE
                        registerBtn.isEnabled = true
                        val errorMessage = task.exception?.localizedMessage ?: "Signup failed. Try again."
                        Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}