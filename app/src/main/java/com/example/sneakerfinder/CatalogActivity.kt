package com.example.sneakerfinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.toolbar.*


class CatalogActivity : AppCompatActivity() {
    var list: ArrayList<Any> = arrayListOf<Any>()
    var listNames: ArrayList<String> = arrayListOf<String>()


    var list_filter: ArrayList<Any> = arrayListOf<Any>()
    var listNames_filter: ArrayList<String> = arrayListOf<String>()

    lateinit var auth: FirebaseAuth
    lateinit var intentToProfile: Intent
    lateinit var map: Map<String, Any>
    lateinit var username: String
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        var text = intent.getStringExtra("email")
        var user = text.split("@")
        username = user[0]
        auth = FirebaseAuth.getInstance()

        showCatalogFromFirestore()

        var tb: androidx.appcompat.widget.Toolbar = my_toolbar

        setSupportActionBar(tb)
        supportActionBar!!.title = username


    }

    private fun showCatalogFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                list.clear()
                listNames.clear()
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    var item = map["brand"].toString() + " " + map["model"].toString()

                    listNames.add(item)
                    list.add(map)
                }
                //val gm = GridLayoutManager( this, 2 )
                val lm = LinearLayoutManager(this)
                val dividerItemDecoration = DividerItemDecoration(
                    rv_catalog.context,
                    lm.orientation
                )
                rv_catalog.addItemDecoration(dividerItemDecoration)
                val adapter = CatalogAdapter(this, list, listNames, username)
                rv_catalog.layoutManager = lm
                rv_catalog.adapter = adapter

            }
            .addOnFailureListener {
            }
    }

    private fun showCatalogFromFirestoreFiltered(text: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                list.clear()
                listNames.clear()
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    var item = map["brand"].toString() + " " + map["model"].toString()
                    if (item.toLowerCase().contains(text)) {
                        listNames.add(item)
                        list.add(map)
                    }
                }
                //val gm = GridLayoutManager( this, 2 )
                val lm = LinearLayoutManager(this)
                val adapter = CatalogAdapter(this, list, listNames, username)
                rv_catalog.layoutManager = lm
                rv_catalog.adapter = adapter

            }
            .addOnFailureListener {
            }
    }

    private fun showCatalogFromFirestoreFilteredAll() {
        val db = FirebaseFirestore.getInstance()

        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                list.clear()
                listNames.clear()
                for (document in documents) {
                    map = document.data as Map<String, Any>
                    var item = map["brand"].toString() + " " + map["model"].toString()

                    listNames.add(item)
                    list.add(map)

                }
                //val gm = GridLayoutManager( this, 2 )
                val lm = LinearLayoutManager(this)
                val adapter = CatalogAdapter(this, list, listNames, username)
                rv_catalog.layoutManager = lm
                rv_catalog.adapter = adapter

            }
            .addOnFailureListener {
            }
    }

    private fun setUpIntentProfile(user: String) {
        var guest = intent.getStringExtra("guest")
        if (guest == "X") {
            showCustomToast("Entered as guest. Cannot access to any profile")
        } else {
            intentToProfile = Intent(this, ProfileActivity::class.java)
            intentToProfile.putExtra("user", user)
            startActivity(intentToProfile)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu?.findItem(R.id.search_action)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            val et_searchview = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            et_searchview.hint = "Filter here"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {

                        val search = newText.toLowerCase()
                        showCatalogFromFirestoreFiltered(search)
                        rv_catalog.adapter?.notifyDataSetChanged()

                    } else {
                        showCatalogFromFirestoreFilteredAll()
                        rv_catalog.adapter?.notifyDataSetChanged()
                    }

                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var text = intent.getStringExtra("email")
        var user = text.split("@")
        when (id) {
            R.id.logout_action -> logout()
            R.id.profile_action -> setUpIntentProfile(user[0])
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        var checkGoogle = intent.getStringExtra("google")

        if (!checkGoogle.isNullOrEmpty()) {

            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    showCustomToast("Signed Out")
                    val intentToLogin = Intent(this, LoginActivity::class.java)
                    startActivity(intentToLogin)
                    finish()
                })


        } else {
            auth.signOut()
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
            finish()
        }

    }

    private fun showCustomToast(msg: CharSequence) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}
