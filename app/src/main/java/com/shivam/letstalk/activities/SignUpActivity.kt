package com.shivam.letstalk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shivam.letstalk.R
import com.shivam.letstalk.custom.launchActivity
import com.shivam.letstalk.util.*
import com.shivam.letstalk.util.Constants.KEY_COLLECTION_USERS
import com.shivam.letstalk.util.Constants.KEY_EMAIL
import com.shivam.letstalk.util.Constants.KEY_FIRST_NAME
import com.shivam.letstalk.util.Constants.KEY_IS_SIGNED_IN
import com.shivam.letstalk.util.Constants.KEY_LAST_NAME
import com.shivam.letstalk.util.Constants.KEY_PASSWORD
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        imageBack.setOnClickListener { onBackPressed() }
        textSignIn.setOnClickListener { onBackPressed() }

        buttonSignUp.setOnClickListener {
            checkValidation()
        }

    }

    private fun checkValidation() {
        if (inputFirstName.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter first name", Toast.LENGTH_LONG).show()
        } else if (inputLastName.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter last name", Toast.LENGTH_LONG).show()
        } else if (inputEmail.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_LONG).show()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getString()).matches()) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show()
        } else if (inputPassword.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter password ", Toast.LENGTH_LONG).show()
        } else if (inputConfirmPassword.getString().trim().isEmpty()) {
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_LONG).show()
        } else if (inputPassword.getString() != inputConfirmPassword.getString()) {
            Toast.makeText(this, "Password & confirm password must be same", Toast.LENGTH_LONG).show()
        } else {
            signUp()
        }
    }

    private fun signUp() {
        signUpProgressBar.visibility = VISIBLE
        buttonSignUp.visibility = INVISIBLE

        val user = hashMapOf(
            KEY_FIRST_NAME to inputFirstName.getString(),
            KEY_LAST_NAME to inputLastName.getString(),
            KEY_EMAIL to inputEmail.getString(),
            KEY_PASSWORD to inputPassword.getString()
        )

        database.collection(KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener { documentReference ->
                putBoolean(KEY_IS_SIGNED_IN, true)
                putString(Constants.KEY_USER_ID, documentReference.id)
                putString(KEY_FIRST_NAME, inputFirstName.getString())
                putString(KEY_LAST_NAME, inputLastName.getString())
                putString(KEY_EMAIL, inputEmail.getString())

                launchActivity<MainActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }

                Toast.makeText(application, "User Inserted", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                signUpProgressBar.visibility = INVISIBLE
                buttonSignUp.visibility = VISIBLE
                Toast.makeText(application, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
    }
}