package com.example.sneakerfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sneaker_detail.*

class SneakerDetailActivity : AppCompatActivity() {

    private var brand: String? = null
    private var model: String? = null
    private var size: String? = null
    private var owner: String? = null
    private var userLogged: String? = null
    lateinit var map: Map<String, Any>
    var addCheck: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sneaker_detail)

        val add = intent.getStringExtra("add")
        val username = intent.getStringExtra("userLogged")
        if (add == "X") {
            addCheck = true
            et_owner_detail_sneaker.setText(username)

        }
        if (!addCheck) {
            getInitialValues()
            setValuesOnScreen()
            checkUserLogged(owner!!)
        } else {
            setEditableFields()
            hideButtonsSneakerHandle()


        }

        bt_delete_sneaker_detail.setOnClickListener {
            showDeleteDialog()
        }
        bt_save_sneaker_detail.setOnClickListener {
            if (add == "X") {
                if (checkFieldsNotEmpty()) {
                    addSneaker()
                } else {
                    showCustomToast("Fill ALL the fields please")
                }
            } else {
                saveChanges(username)
            }

        }
        bt_edit_sneaker_detail.setOnClickListener {
            setEditableFields()
        }
        bt_owner_sneaker_detail.setOnClickListener {
            goToOwner()
        }
    }

    private fun hideButtonsSneakerHandle() {
        bt_delete_sneaker_detail.isEnabled = false
        bt_edit_sneaker_detail.isEnabled = false
        bt_owner_sneaker_detail.isEnabled = false

        bt_delete_sneaker_detail.isVisible = false
        bt_edit_sneaker_detail.isVisible = false
        bt_owner_sneaker_detail.isVisible = false
    }

    private fun goToOwner() {
        var intentToOwner = Intent(this, ProfileActivity::class.java)
        intentToOwner.putExtra("owner_sneaker", "X")
        intentToOwner.putExtra("user", et_owner_detail_sneaker.text.toString())
        intentToOwner.putExtra("brand", et_brand_detail_sneaker.text.toString())
        intentToOwner.putExtra("model", et_model_detail_sneaker.text.toString())
        intentToOwner.putExtra("size", et_size_detail_sneaker.text.toString())
        startActivity(intentToOwner)
    }


    private fun checkFieldsNotEmpty(): Boolean {
        return et_brand_detail_sneaker.text.isNotEmpty()
                && et_model_detail_sneaker.text.isNotEmpty()
                && et_owner_detail_sneaker.text.isNotEmpty()
                && et_size_detail_sneaker.text.isNotEmpty()
    }

    private fun addSneaker() {
        val db = FirebaseFirestore.getInstance()
        val data = mapOf(
            "brand" to et_brand_detail_sneaker.text.toString(),
            "model" to et_model_detail_sneaker.text.toString(),
            "owner" to et_owner_detail_sneaker.text.toString(),
            "size" to et_size_detail_sneaker.text.toString()
        )

        db.collection("sneakers")
            .add(data)
            .addOnSuccessListener { documentReference ->
                showCustomToast("Sneaker Added")
                startActivity(Intent(this, MySneakersActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showCustomToast("Something went wrong, try again later")
                Log.e("error adding sneaker", e.message.toString())
            }
    }

    private fun saveChanges(username: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .whereEqualTo("brand", brand)
            .whereEqualTo("model", model)
            .whereEqualTo("size", size)
            .whereEqualTo("owner", owner)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update(
                        mapOf(
                            "brand" to et_brand_detail_sneaker.text.toString(),
                            "model" to et_model_detail_sneaker.text.toString(),
                            "owner" to et_owner_detail_sneaker.text.toString(),
                            "size" to et_size_detail_sneaker.text.toString()
                        )
                    ).addOnSuccessListener {
                        showCustomToast("Sneaker updated")
                        setNotEditableFields()
                        var intentBackToCatalog = Intent(this, CatalogActivity::class.java)
                        intentBackToCatalog.putExtra("email", username)
                        startActivity(intentBackToCatalog)
                        finish()
                    }

                }
            }
            .addOnFailureListener { e ->
                showCustomToast("ERROR : " + e.message)
            }

    }

    private fun setEditableFields() {
        et_brand_detail_sneaker.isEnabled = true
        et_model_detail_sneaker.isEnabled = true
        et_owner_detail_sneaker.isEnabled = true
        et_size_detail_sneaker.isEnabled = true
    }

    private fun setNotEditableFields() {
        et_brand_detail_sneaker.isEnabled = false
        et_model_detail_sneaker.isEnabled = false
        et_owner_detail_sneaker.isEnabled = false
        et_size_detail_sneaker.isEnabled = false
    }

    private fun setValuesOnScreen() {
        if (!brand.isNullOrEmpty()) {
            et_brand_detail_sneaker.setText(brand)
            et_model_detail_sneaker.setText(model)
            et_size_detail_sneaker.setText(size)
            et_owner_detail_sneaker.setText(owner)
        }

    }

    private fun getInitialValues() {
        brand = intent.getStringExtra("brand")
        model = intent.getStringExtra("model")
        size = intent.getStringExtra("size")
        owner = intent.getStringExtra("owner")
    }

    private fun checkUserLogged(owner: String) {
        userLogged = intent.getStringExtra("userLogged")
        if (!userLogged.equals(owner)) {
            setButInvisibleNotClickable()
        } else {
            bt_owner_sneaker_detail.isClickable = false
            bt_owner_sneaker_detail.isEnabled = false
            bt_owner_sneaker_detail.isVisible = false
        }
    }

    private fun setButInvisibleNotClickable() {
        bt_edit_sneaker_detail.isEnabled = false
        bt_edit_sneaker_detail.isVisible = false
        bt_edit_sneaker_detail.isClickable = false

        bt_save_sneaker_detail.isEnabled = false
        bt_save_sneaker_detail.isVisible = false
        bt_save_sneaker_detail.isClickable = false

        bt_delete_sneaker_detail.isClickable = false
        bt_delete_sneaker_detail.isEnabled = false
        bt_delete_sneaker_detail.isVisible = false

        et_brand_detail_sneaker.isEnabled = false
        et_model_detail_sneaker.isEnabled = false
        et_size_detail_sneaker.isEnabled = false
        et_owner_detail_sneaker.isEnabled = false
    }

    private fun showDeleteDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete the $brand $model?")
        builder.setNegativeButton("Yes") { dialog, which ->
            deleteSneaker()
        }

        builder.setPositiveButton("No") { dialog, which ->
            Toast.makeText(this, "Action cancelled", Toast.LENGTH_SHORT).show()
        }


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteSneaker() {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .whereEqualTo("brand", brand)
            .whereEqualTo("model", model)
            .whereEqualTo("size", size)
            .whereEqualTo("owner", owner)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete().addOnSuccessListener {
                        Toast.makeText(this, "Sneaker deleted", Toast.LENGTH_SHORT).show()
                        var intentToCatalog = Intent(this, CatalogActivity::class.java)
                        startActivity(intentToCatalog)
                        finish()
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
