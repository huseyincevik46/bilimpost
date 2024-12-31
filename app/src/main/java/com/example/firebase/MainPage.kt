    package com.example.firebase


    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import android.view.Menu
    import android.view.MenuItem
    import android.widget.Toast
    import com.google.firebase.auth.FirebaseAuth

    class MainPage : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main_page)
            auth = FirebaseAuth.getInstance()
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.postmenu, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.postekle -> {
                    Log.d("MainPage", "Postekle menüsüne tıklandı")
                    try {
                        Log.d("MainPage", "Intent oluşturuluyor")
                        val intent = Intent(this@MainPage, Postekle::class.java)
                        Log.d("MainPage", "startActivity çağrılıyor")
                        startActivity(intent)
                        Log.d("MainPage", "startActivity çağrıldı")
                        true
                    } catch (e: Exception) {
                        Log.e("MainPage", "Hata: ", e)
                        Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                        false
                    }
                }
                R.id.exit -> {
                    // Firebase'den çıkış yap
                    auth.signOut()
                    // Login sayfasına yönlendir
                    val intent = Intent(this, loginPage::class.java)
                    startActivity(intent)
                    // MainPage'i kapat
                    finish()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }
    }