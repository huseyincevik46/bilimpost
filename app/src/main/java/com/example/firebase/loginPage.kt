package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class loginPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
        auth = Firebase.auth

        var mail = findViewById<EditText>(R.id.emailEditText)
        var password = findViewById<EditText>(R.id.passwordEditText)
        var kaydetButton = findViewById<Button>(R.id.registerButton)
        var loginbutton = findViewById<Button>(R.id.loginButton)

        kaydetButton.setOnClickListener {
            auth.createUserWithEmailAndPassword(mail.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                    // Kayıt başarılı olunca MainPage'e yönlendir
                    val intent = Intent(this@loginPage, MainPage::class.java)
                    startActivity(intent)
                    finish() // LoginPage'i kapat
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }

        loginbutton.setOnClickListener {
            auth.signInWithEmailAndPassword(mail.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    // Giriş başarılı olunca MainPage'e yönlendir
                    val intent = Intent(this@loginPage, MainPage::class.java)
                    startActivity(intent)
                    finish() // LoginPage'i kapat
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Kullanıcı zaten giriş yapmışsa direkt MainPage'e yönlendir
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@loginPage, MainPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}