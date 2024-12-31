package com.example.firebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Postekle : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var surnameEditText: TextInputEditText
    private lateinit var birthPlaceEditText: TextInputEditText
    private lateinit var birthDateEditText: TextInputEditText
    private lateinit var deathDateEditText: TextInputEditText
    private lateinit var contributionsEditText: TextInputEditText
    private lateinit var saveButton: Button
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postekle)

        // View'ları initialize et
        initializeViews()
        // Click listener'ları ayarla
        setupClickListeners()
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.imageView)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthPlaceEditText = findViewById(R.id.birthPlaceEditText)
        birthDateEditText = findViewById(R.id.birthDateEditText)
        deathDateEditText = findViewById(R.id.deathDateEditText)
        contributionsEditText = findViewById(R.id.contributionsEditText)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun setupClickListeners() {
        imageView.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        saveButton.setOnClickListener {
            validateAndSaveData()
        }
    }

    private fun checkPermission(): Boolean {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                imageView.setImageURI(it)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "İzin vermeniz gerekiyor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateAndSaveData() {
        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val birthPlace = birthPlaceEditText.text.toString().trim()
        val birthDate = birthDateEditText.text.toString().trim()
        val deathDate = deathDateEditText.text.toString().trim()
        val contributions = contributionsEditText.text.toString().trim()

        if (name.isBlank()) {
            nameEditText.error = "Ad alanı boş bırakılamaz"
            return
        }
        if (surname.isBlank()) {
            surnameEditText.error = "Soyad alanı boş bırakılamaz"
            return
        }

        saveDataToFirebase(selectedImageUri, name, surname, birthPlace, birthDate, deathDate, contributions)
    }

    private fun saveDataToFirebase(
        imageUri: Uri?,
        name: String,
        surname: String,
        birthPlace: String,
        birthDate: String,
        deathDate: String,
        contributions: String
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val firestoreReference = FirebaseFirestore.getInstance()

        if (imageUri != null) {
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val imageRef = storageReference.child(fileName)

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        val postData = hashMapOf(
                            "name" to name,
                            "surname" to surname,
                            "birthPlace" to birthPlace,
                            "birthDate" to birthDate,
                            "deathDate" to deathDate,
                            "contributions" to contributions,
                            "imageUrl" to imageUrl.toString()
                        )
                        firestoreReference.collection("posts")
                            .add(postData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Veriler kaydedildi.", Toast.LENGTH_SHORT).show()
                                clearForm()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Veritabanına kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Resim yüklenirken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Lütfen bir resim seçin.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        imageView.setImageResource(R.drawable.ic_launcher_background)
        nameEditText.text?.clear()
        surnameEditText.text?.clear()
        birthPlaceEditText.text?.clear()
        birthDateEditText.text?.clear()
        deathDateEditText.text?.clear()
        contributionsEditText.text?.clear()
        selectedImageUri = null
    }
}
