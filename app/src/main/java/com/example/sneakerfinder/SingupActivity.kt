package com.example.sneakerfinder

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_singup.*

class SingupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        //Getting firebase instance
        auth = FirebaseAuth.getInstance()

        bt_sign_up_sign_up.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser(){
        //Check obligatory fields
        if (checkUser()) {
            setRedObligatoryFields()
            showCustomToast("Fulfill the obligatory fields")
            return
        }
        //Check valid email address pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(et_mail_sign_up.text.toString()).matches()) {
            showCustomToast("Enter a valid email")
            return
        }

        auth.createUserWithEmailAndPassword(et_mail_sign_up.text.toString(), et_paswd_sign_up.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Signup is successful ,we send verification mail and we return to login activity
                    var user = auth.currentUser;

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this,LoginActivity::class.java))
                                showCustomToast("Sing Up Successful")
                                finish()
                            }
                        }
                } else {
                    // Something went wrong
                    showCustomToast("Authentication failed. Try again after some time...")
                    Log.e("ERROR SIGNUP ", task.exception.toString())
                }


            }



    }

    private fun checkUser(): Boolean {
        return et_mail_sign_up.text.isEmpty() || et_paswd_sign_up.text.isEmpty()
    }

    private fun setRedObligatoryFields() {
        et_mail_sign_up.setHintTextColor(Color.RED)
        et_paswd_sign_up.setHintTextColor(Color.RED)
    }

    private fun showCustomToast(msg: CharSequence){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
