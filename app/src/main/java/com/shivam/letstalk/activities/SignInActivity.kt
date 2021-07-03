package com.shivam.letstalk.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shivam.letstalk.R
import com.shivam.letstalk.custom.launchActivity
import com.shivam.letstalk.util.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.inputEmail
import kotlinx.android.synthetic.main.activity_sign_in.inputPassword
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignInActivity : AppCompatActivity() {
    private val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if(retrieveBoolean(Constants.KEY_IS_SIGNED_IN)){
            launchActivity<MainActivity>()
            finish()
        }

        textSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

        buttonSignIn.setOnClickListener {
            checkValidation()
        }

    }

    private fun checkValidation() {
        if (inputEmail.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_LONG).show()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getString()).matches()) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show()
        } else if (inputPassword.getString().trim().isEmpty()) {
            Toast.makeText(this, "Enter password ", Toast.LENGTH_LONG).show()
        } else {
            signIn()
        }
    }

    private fun signIn() {
        signInProgressBar.visibility = View.VISIBLE
        buttonSignIn.visibility = View.INVISIBLE

        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getString())
            .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getString())
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result!=null && task.result!!.documents.size > 0){
                    task.result!!.documents.let {
                        val documentSnapshot = it[0]

                        putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                        putString(Constants.KEY_USER_ID, documentSnapshot.id)
                        putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME))
                        putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME))
                        putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL))

                        launchActivity<MainActivity> {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    }
                }else{
                    signInProgressBar.visibility = View.INVISIBLE
                    buttonSignIn.visibility = View.VISIBLE
                    Toast.makeText(application, "Unable to signIn!", Toast.LENGTH_SHORT).show()
                }
            }

    }
}