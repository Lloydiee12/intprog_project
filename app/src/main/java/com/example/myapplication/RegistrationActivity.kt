package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Ensure Firebase is initialized
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Get the UID of the registered user
                val userId = auth.currentUser?.uid

                // Reference to the Firebase Realtime Database with the correct URL
                val database = FirebaseDatabase.getInstance("https://intprog-pagobo-default-rtdb.asia-southeast1.firebasedatabase.app")
                val userRef = database.reference.child("users").child(userId!!)

                // Create a map of user details
                val userMap = mapOf(
                    "email" to email
                    // You can add more user details here if needed (e.g., name, age, etc.)
                )

                // Save user data under the "users" node, using the user's UID as the key
                userRef.setValue(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration Successful and User Data Saved", Toast.LENGTH_SHORT).show()
                        finish() // Go back to login or home activity
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RegistrationActivity", "Error saving user data", exception)
                        Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("RegistrationActivity", "Error during registration", exception)
                Toast.makeText(this, "Registration Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
