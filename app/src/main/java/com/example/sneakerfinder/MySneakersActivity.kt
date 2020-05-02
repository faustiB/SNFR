package com.example.sneakerfinder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_sneakers.*

class MySneakersActivity : AppCompatActivity() {

    var list: ArrayList<Any> = arrayListOf<Any>()
    var listNames: ArrayList<String> = arrayListOf<String>()
    lateinit var auth: FirebaseAuth
    lateinit var map: Map<String, Any>
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sneakers)

        username = intent.getStringExtra("user")

        auth = FirebaseAuth.getInstance()

        showCatalogFromFirestore()
        bt_add_sneaker.setOnClickListener {
            var intentToAdd = Intent(this@MySneakersActivity, SneakerDetailActivity::class.java)
            intentToAdd.putExtra("add", "X")
            intentToAdd.putExtra("userLogged", username)
            startActivity(intentToAdd)
        }

    }

    private fun showCatalogFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    var item = map["brand"].toString() + " " + map["model"].toString()
                    var ownerDb = map["owner"].toString()
                    if (username.equals(ownerDb)) {
                        listNames.add(item)
                        list.add(map)
                    }
                }

                val lm = LinearLayoutManager(this)
                val dividerItemDecoration = DividerItemDecoration(
                    rv_my_sneakers.context,
                    lm.orientation
                )
                val adapter = MySneakersAdapter(this@MySneakersActivity, list, listNames, username)
                rv_my_sneakers.addItemDecoration(dividerItemDecoration)
                rv_my_sneakers.layoutManager = lm
                rv_my_sneakers.adapter = adapter


            }
            .addOnFailureListener {
            }
    }


}

