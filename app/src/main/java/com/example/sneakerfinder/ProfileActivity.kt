package com.example.sneakerfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {


    lateinit var map: Map<String, Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        var user = intent.getStringExtra("user")
        setUserData(user)

        val owner = intent.getStringExtra("owner_sneaker")
        if (owner == "X") {
            setButtonsForNotOwner()
        }

        bt_edit_profile.setOnClickListener {
            et_phone_profile.isEnabled = true
        }
        bt_save_profile.setOnClickListener {
            saveChangesProfile(user)
            et_phone_profile.isEnabled = false
        }
        bt_sneakers_show_profile.setOnClickListener {
            intentToMySneaker(user)
        }
        bt_call_show_profile.setOnClickListener {
            startCall()
        }
        bt_send_email_show_profile.setOnClickListener {
            sendEmail()
        }

    }

    private fun startCall() {
        var intentToCall =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + et_phone_profile.text.toString()))
        startActivity(intentToCall)
    }

    private fun sendEmail() {
        var brand = intent.getStringExtra("brand")
        var model = intent.getStringExtra("model")
        var size = intent.getStringExtra("size")

        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", et_email_profile.text.toString() + "@gmail.com", null)
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$brand $model $size")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, \n I would like to ask about the sneaker $brand $model " +
                "in the size : $size \n \n  " +
                "\n \nBest regards;")
        startActivity(emailIntent)
    }

    private fun setButtonsForNotOwner() {
        bt_send_email_show_profile.isClickable = true
        bt_send_email_show_profile.isEnabled = true
        bt_send_email_show_profile.isVisible = true

        bt_call_show_profile.isClickable = true
        bt_call_show_profile.isEnabled = true
        bt_call_show_profile.isVisible = true

        bt_sneakers_show_profile.isClickable = false
        bt_sneakers_show_profile.isEnabled = false
        bt_sneakers_show_profile.isVisible = false

        bt_edit_profile.isClickable = false
        bt_edit_profile.isEnabled = false
        bt_edit_profile.isVisible = false

        bt_save_profile.isClickable = false
        bt_save_profile.isEnabled = false
        bt_save_profile.isVisible = false
    }

    private fun intentToMySneaker(user: String) {
        var intentToMySneakers = Intent(this, MySneakersActivity::class.java)
        intentToMySneakers.putExtra("user", user)
        startActivity(intentToMySneakers)
    }

    private fun setUserData(user: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users_phones").whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    val phoneNum = map["phone"].toString()
                    val userName = map["user"].toString()
                    if (userName == user) {
                        et_phone_profile.setText(phoneNum)
                        et_email_profile.setText(userName)

                        et_phone_profile.isEnabled = false
                    }


                }
            }
            .addOnFailureListener { e ->
                showCustomToast("ERROR : " + e.message)
            }
    }

    private fun saveChangesProfile(user: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users_phones").whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    val userName = map["user"].toString()
                    if (userName == user) {
                        document.reference.update(
                            mapOf(
                                "phone" to et_phone_profile.text.toString()
                            )
                        ).addOnSuccessListener {
                            showCustomToast("User updated ")
                        }.addOnFailureListener { e ->
                            Log.e("Error UPDATE ", e.toString())
                        }
                    }


                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "ERROR : " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCustomToast(msg: CharSequence) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}
