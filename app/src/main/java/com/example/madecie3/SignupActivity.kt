package com.example.madecie3

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class SignupActivity : AppCompatActivity() {

    private val avatarPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val preview = findViewById<ImageView>(R.id.avatarPreview)
                val placeholder = findViewById<LinearLayout>(R.id.avatarPlaceholder)
                preview.setImageURI(it)
                preview.visibility = View.VISIBLE
                placeholder.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name            = findViewById<EditText>(R.id.name)
        val email           = findViewById<EditText>(R.id.email)
        val password        = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn     = findViewById<Button>(R.id.registerBtn)
        val progressBar     = findViewById<ProgressBar>(R.id.signupProgress)
        val avatarPickArea  = findViewById<FrameLayout>(R.id.avatarPickArea)
        val auth            = FirebaseAuth.getInstance()

        avatarPickArea.setOnClickListener {
            avatarPickerLauncher.launch("image/*")
        }

        registerBtn.setOnClickListener {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection detected", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
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
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(this@SignupActivity, "Account created!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val error = profileTask.exception?.localizedMessage ?: "Failed to set display name"
                                    Toast.makeText(this@SignupActivity, "Account created, but profile update failed: $error", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this@SignupActivity, DashboardActivity::class.java))
                                    finish()
                                }
                            }
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Signup failed. Try again."
                        Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}