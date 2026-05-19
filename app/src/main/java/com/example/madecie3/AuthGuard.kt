package com.example.madecie3

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthGuard {
    fun requireAuthenticated(activity: AppCompatActivity): FirebaseUser? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) return user

        Toast.makeText(activity, "Please login to continue", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
        return null
    }
}
