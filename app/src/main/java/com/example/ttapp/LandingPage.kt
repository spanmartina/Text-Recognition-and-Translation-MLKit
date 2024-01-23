package com.example.ttapp

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LandingPage : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var reference: DatabaseReference

        private lateinit var nameTextView: TextView
        private lateinit var usernameTextView: TextView
        private lateinit var ocrCard: CardView
        private var imageUri: Uri? = null
        private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                startPreviewActivity(imageUri)
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

            nameTextView = findViewById(R.id.name)
            usernameTextView = findViewById(R.id.username)

            // Initialize Firebase components
            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            reference = database.reference.child("users")

            // Fetch data from Realtime Database
            val currentUser = auth.currentUser
            currentUser?.uid?.let { userId ->
                reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").getValue(String::class.java)
                        val username = snapshot.child("username").getValue(String::class.java)

                        // Update UI
                        nameTextView.text = name
                        usernameTextView.text = username
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                    }
                })
            }
            //OCR card view
            ocrCard = findViewById(R.id.ocrCard)

            ocrCard.setOnClickListener {
                showOcrSelectionDialog()
            }
            //Translator
            val translatorCard: CardView = findViewById(R.id.translatorCard)
            translatorCard.setOnClickListener {
                val intent = Intent(this@LandingPage, TextTranslator::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
    }
    private fun showOcrSelectionDialog() {
        val btnUpload: RelativeLayout
        val btnCamera: RelativeLayout

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ocr_selection)

        btnCamera = dialog.findViewById(R.id.btnCamera)
        btnUpload = dialog.findViewById(R.id.btnUpload)

        btnUpload.setOnClickListener {
            pickImageFromGallery()
            dialog.dismiss()
        }

        btnCamera.setOnClickListener {
            val intent = Intent(this@LandingPage, CameraActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            // Launch the intent
            pickImageLauncher.launch(intent)
        }
//        startActivityForResult(intent, STORAGE_REQUEST_CODE)
    }

    // Add this function to start the PreviewActivity with the selected image URI
    private fun startPreviewActivity(imageUri: Uri?) {
        if (imageUri != null) {
            Log.e("startPreviewActivity URI!!!", imageUri.toString())
            val intent = Intent(this@LandingPage, PreviewFromGalleryActivity::class.java)
            intent.putExtra(PreviewFromGalleryActivity.EXTRA_IMAGE_URI, imageUri)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
//        else {
            // Handle the case where imageUri is null
//            showToast("Selected image URI is null", Toast.LENGTH_SHORT).show()
//        }
    }
}