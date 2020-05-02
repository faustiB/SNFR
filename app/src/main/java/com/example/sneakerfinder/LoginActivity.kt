package com.example.sneakerfinder

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


const val RC_SIGN_IN = 123
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var loginFailed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        bt_sign_in_google.setSize(SignInButton.SIZE_WIDE)
        bt_sign_in_google.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            showCustomToast("Login sucessful")
            var intentToCat = Intent(this, CatalogActivity::class.java)
            intentToCat.putExtra("email", acct.email)
            intentToCat.putExtra("google", "X")
            startActivity(intentToCat)
            finish()
        }

        //Getting firebase instance
        auth = FirebaseAuth.getInstance()
        bt_sign_up.setOnClickListener {
            startActivity(Intent(this, SingupActivity::class.java))
            finish()
        }

        bt_log_in.setOnClickListener {
            login()
        }

        bt_forgot_pswd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Send email to reset password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val emailUser = view.findViewById<EditText>(R.id.et_email_dialog_fp)
            builder.setView(view)

            builder.setPositiveButton(
                "Reset",
                DialogInterface.OnClickListener { _, _ -> forgotPassword(emailUser) })
            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { _, _ -> showCustomToast("Reset Cancelled") })
            builder.show()

        }
        bt_invited.setOnClickListener {
            var intentToCatalogGuest = Intent(this@LoginActivity,CatalogActivity::class.java)
            intentToCatalogGuest.putExtra("guest" ,"X")
            intentToCatalogGuest.putExtra("email","guest")
            startActivity(intentToCatalogGuest)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // The Task returned from this call is always completed, no need to attach
// a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.


            if (account != null) {
                showCustomToast("Login sucessful")
                var intentToCat = Intent(this, CatalogActivity::class.java)
                intentToCat.putExtra("email", account.email)
                intentToCat.putExtra("google", "X")
                startActivity(intentToCat)
                finish()
            }

        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null)
        }
    }

    private fun forgotPassword(email: EditText) {
        if (email.text.toString().isNullOrEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            return
        }
        auth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showCustomToast("Email sent, check your inbox ")
                } else {
                    showCustomToast("Something went wrong , try again ")
                }
            }

    }

    private fun login() {
        if (checkUser()) {
            setRedObligatoryFields()
            showCustomToast("Fulfill the obligatory fields")
            return
        }
        //Check valid email address pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()) {
            showCustomToast("Enter a valid email")
            return
        }
        loginFailed = false
        auth.signInWithEmailAndPassword(et_email.text.toString(), et_pswd.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Login Failed
                    loginFailed = true
                    updateUI(null)
                }

            }

    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                showCustomToast("Login sucessful")
                var intentToCat = Intent(this, CatalogActivity::class.java)
                intentToCat.putExtra("email", currentUser.email)
                startActivity(intentToCat)
                finish()
            } else {
                showCustomToast("Please check your inbox, and verify your email address ")
            }

        } else {
            if (loginFailed) {
                showCustomToast("Authentication failed")
            }

        }
    }

    private fun checkUser(): Boolean {
        return et_pswd.text.isEmpty() || et_email.text.isEmpty()
    }

    private fun setRedObligatoryFields() {
        et_email.setHintTextColor(Color.RED)
        et_pswd.setHintTextColor(Color.RED)
    }

    private fun showCustomToast(msg: CharSequence) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
