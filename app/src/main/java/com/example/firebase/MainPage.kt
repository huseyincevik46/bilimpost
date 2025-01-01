package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        loadPosts()
    }

    private fun loadPosts() {
        val firestoreReference = FirebaseFirestore.getInstance()
        firestoreReference.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Veriler yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    postList.clear()
                    for (document in snapshot.documents) {
                        val post = document.toObject(Post::class.java)
                        post?.let { postList.add(it) }
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.postmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.postekle -> {
                val intent = Intent(this@MainPage, Postekle::class.java)
                startActivity(intent)
                return true
            }
            R.id.exit -> {
                auth.signOut()
                Toast.makeText(this, "Çıkış yapıldı.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, loginPage::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}