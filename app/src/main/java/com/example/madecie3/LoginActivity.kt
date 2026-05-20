package com.example.madecie3

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import android.content.pm.PackageManager
import android.os.Build

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var googleLoginBtn: Button
    private lateinit var deliveryPartnerLoginBtn: Button
    private lateinit var signupText: TextView
    private lateinit var progressBar: ProgressBar

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                setLoading(false)
                return@registerForActivityResult
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrEmpty()) {
                    setLoading(false)
                    Toast.makeText(this, "Google sign-in token missing. Ensure correct SHA-1 in Firebase.", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "ID Token is null or empty. Account: ${account.email}")
                    return@registerForActivityResult
                }
                firebaseAuthWithGoogle(idToken)
            } catch (e: ApiException) {
                setLoading(false)
                val statusMessage = when (e.statusCode) {
                    7 -> "Connectivity error (7). Check emulator internet or Airplane mode."
                    8 -> "Internal error (8). Try restarting the emulator."
                    10 -> "Developer error (10). Verify SHA-1 and package name match exactly in Firebase."
                    12500 -> "Sign-in failed (12500). Update Google Play Services."
                    12501 -> "Sign-in cancelled by user."
                    else -> "Google sign-in failed (${e.statusCode}): ${e.localizedMessage}"
                }
                Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Google Sign-In ApiException (Status: ${e.statusCode})", e)
                if (e.statusCode == 7) {
                    Log.d("LoginActivity", "Network available: ${isNetworkAvailable()}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logAppSignature()

        auth = FirebaseAuth.getInstance()
        logFirebaseConfig()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        googleLoginBtn = findViewById(R.id.googleLoginBtn)
        deliveryPartnerLoginBtn = findViewById(R.id.deliveryPartnerLoginBtn)
        signupText = findViewById(R.id.signupText)
        progressBar = findViewById(R.id.loginProgress)

        // Theme toggle
        val themeToggleBtn = findViewById<ImageButton>(R.id.themeToggleBtn)
        updateThemeToggleIcon(themeToggleBtn)
        themeToggleBtn.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        val webClientIdResId = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (webClientIdResId == 0) {
            googleLoginBtn.isEnabled = false
            Toast.makeText(this, "Google Sign-In not configured. Check google-services setup.", Toast.LENGTH_LONG).show()
        } else {
            val webClientId = getString(webClientIdResId)
            Log.d("LoginActivity", "Using Web Client ID: $webClientId")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
        }

        loginBtn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText  = password.text.toString().trim()
            if (emailText.isEmpty()) { email.error = "Enter email"; return@setOnClickListener }
            if (passText.isEmpty())  { password.error = "Enter password"; return@setOnClickListener }
            setLoading(true)
            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this) { task ->
                    setLoading(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        if (emailText == "deliverypartner@gmail.com") {
                            startActivity(Intent(this@LoginActivity, PartnerShipmentsActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        }
                        finish()
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Invalid credentials"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        googleLoginBtn.setOnClickListener {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection detected", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (!::googleSignInClient.isInitialized) {
                Toast.makeText(this, "Google Sign-In not configured", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            setLoading(true)
            // Refresh stale sessions and always request a fresh account token.
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }

        deliveryPartnerLoginBtn.setOnClickListener {
            setLoading(true)
            val email = "deliverypartner@gmail.com"
            val pass = "deliverypartner123"
            
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        setLoading(false)
                        Toast.makeText(this@LoginActivity, "Delivery Partner Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, DeliveryDashboardActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, try to create the account (first time login)
                        auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(this) { createTask ->
                                setLoading(false)
                                if (createTask.isSuccessful) {
                                    Toast.makeText(this@LoginActivity, "Delivery Partner Account created & logged in!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity, DeliveryDashboardActivity::class.java))
                                    finish()
                                } else {
                                    val errorMessage = task.exception?.localizedMessage ?: "Invalid credentials"
                                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun updateThemeToggleIcon(btn: ImageButton) {
        if (ThemeUtils.isDark(this)) {
            btn.setImageResource(android.R.drawable.ic_menu_day)
        } else {
            btn.setImageResource(android.R.drawable.ic_menu_day)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    if (auth.currentUser?.email == "deliverypartner@gmail.com") {
                        startActivity(Intent(this, PartnerShipmentsActivity::class.java))
                    } else {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    finish()
                } else {
                    val errorMessage = when (val ex = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            "Google auth failed: invalid token. Check Firebase Google provider, SHA-1, and google-services.json."
                        }
                        is FirebaseNetworkException -> "Google auth failed: network issue. Check connectivity and retry."
                        else -> ex?.localizedMessage ?: "Google authentication failed"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Firebase Google auth failed", task.exception)
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginBtn.isEnabled = !isLoading
        googleLoginBtn.isEnabled = !isLoading
        deliveryPartnerLoginBtn.isEnabled = !isLoading
        signupText.isEnabled = !isLoading
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun logAppSignature() {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.signingCertificateHistory
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                val sha1 = md.digest().joinToString(":") { String.format("%02X", it) }
                Log.d("AppSignature", "---- COPY THIS SHA-1 TO FIREBASE CONSOLE ----")
                Log.d("AppSignature", sha1)
                Log.d("AppSignature", "---------------------------------------------")
            }
        } catch (e: Exception) {
            Log.e("AppSignature", "Error getting signature", e)
        }
    }

    private fun logFirebaseConfig() {
        runCatching {
            val options = FirebaseApp.getInstance().options
            Log.d("FirebaseConfig", "Project ID: ${options.projectId}")
            Log.d("FirebaseConfig", "App ID: ${options.applicationId}")
            Log.d("FirebaseConfig", "Storage Bucket: ${options.storageBucket}")
        }.onFailure {
            Log.e("FirebaseConfig", "Unable to read Firebase options", it)
        }
    }
}