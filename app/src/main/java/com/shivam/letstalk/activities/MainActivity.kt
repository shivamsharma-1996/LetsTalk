package com.shivam.letstalk.activities

import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.shivam.letstalk.R
import com.shivam.letstalk.adapters.UserAdapter
import com.shivam.letstalk.custom.launchActivity
import com.shivam.letstalk.listeners.UsersListener
import com.shivam.letstalk.models.User
import com.shivam.letstalk.util.Constants.KEY_COLLECTION_USERS
import com.shivam.letstalk.util.Constants.KEY_EMAIL
import com.shivam.letstalk.util.Constants.KEY_FCM_TOKEN
import com.shivam.letstalk.util.Constants.KEY_FIRST_NAME
import com.shivam.letstalk.util.Constants.KEY_LAST_NAME
import com.shivam.letstalk.util.Constants.KEY_USER_ID
import com.shivam.letstalk.util.clearPreferences
import com.shivam.letstalk.util.retrieveString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UsersListener {
    private val database = Firebase.firestore
    private val usersList = ArrayList<User>();
    private lateinit var usersAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTitle.text = String.format(
            "%s %s", retrieveString(KEY_FIRST_NAME),
            retrieveString(KEY_LAST_NAME)
        )

        setupAndPopulateUserList()
        swipeRefreshLayout.setOnRefreshListener { getUsers() }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                sendFcmToken(it.result.toString())
            }
        }

        tvSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun setupAndPopulateUserList() {
        userRecyclerView.apply {
            setHasFixedSize(true)
            usersAdapter = UserAdapter(usersList, this@MainActivity);
            adapter = usersAdapter
        }

        getUsers()
    }

    private fun getUsers() {
        swipeRefreshLayout.isRefreshing = true
        database.collection(KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                swipeRefreshLayout.isRefreshing = false

                val myUserId = retrieveString(KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    usersList.clear()

                    task.result!!.documents.filter { it.id != myUserId }
                        .forEach { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                var user = User()
                                user.email = documentSnapshot.getString(KEY_EMAIL)
                                user.firstName = documentSnapshot.getString(KEY_FIRST_NAME)
                                user.lastName = documentSnapshot.getString(KEY_LAST_NAME)
                                user.token = documentSnapshot.getString(KEY_FCM_TOKEN)

                                usersList.add(user)
                            }
                        }
                    if(usersList.size>0){
                        usersAdapter.notifyDataSetChanged()
                    }else {
                        tvErrorMessage.text = String.format("%s", "No users available")
                        tvErrorMessage.visibility = VISIBLE
                    }
                }else{
                    tvErrorMessage.text = String.format("%s", "No users available")
                    tvErrorMessage.visibility = VISIBLE
                }

            }
    }

    private fun sendFcmToken(token: String) {
        val documentReference = database.collection(KEY_COLLECTION_USERS)
            .document(retrieveString(KEY_USER_ID))

        documentReference.update(KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
//                Toast.makeText(this, "Token updated successfully!", Toast.LENGTH_LONG)
//                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Unable to send token" + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun signOut() {
        Toast.makeText(this, "Signing Out...", Toast.LENGTH_LONG).show()

        val documentReference = database.collection(KEY_COLLECTION_USERS)
            .document(retrieveString(KEY_USER_ID))

        val updates: HashMap<String, Any> = hashMapOf(
            KEY_FCM_TOKEN to FieldValue.delete()
        )

        documentReference.update(updates)
            .addOnSuccessListener {
                clearPreferences()
                launchActivity<SignInActivity>()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Unable to sign out!", Toast.LENGTH_LONG).show()
            }
    }

    override fun initiateVideoMeeting(user: User) {
            if(user.token== null || user.token!!.trim().isEmpty()){
                Toast.makeText(this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_LONG).show()
            }else{
//                Toast.makeText(this,
//                    "Video meeting with " + user.firstName + " " + user.lastName,
//                    Toast.LENGTH_LONG).show()
                launchActivity<OutgoingInvitationActivity> {
                    putExtra("user", user)
                    putExtra("type", "video")
                }
            }
        }

    override fun initiateAudioMeeting(user: User) {
        if(user.token==null || user.token!!.trim().isEmpty()){
            Toast.makeText(this,
                user.firstName + " " + user.lastName + " is not available for meeting",
                Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,
                "Audio meeting with " + user.firstName + " " + user.lastName,
                Toast.LENGTH_LONG).show()
        }
    }
}