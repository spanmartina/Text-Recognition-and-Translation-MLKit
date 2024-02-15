package com.example.ttapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class LandingPage : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var reference: DatabaseReference
        private lateinit var nameTextView: TextView
        private lateinit var usernameTextView: TextView
        private lateinit var profileImage: CircleImageView
        private var profileImageUrl: String? = null
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
            profileImage = findViewById(R.id.profileImage)

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
                        profileImageUrl = snapshot.child("profileImage").getValue(String::class.java)
                        // Update UI
                        nameTextView.text = name
                        usernameTextView.text = "@$username"

                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this@LandingPage)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user_profile) // Placeholder image
                                .into(profileImage)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("onCancelled", error.message)
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
            }

            //Saved notes
            val savedCard: CardView = findViewById(R.id.savedCard)
            savedCard.setOnClickListener {
                startActivity(Intent(this@LandingPage, SavedNotesActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            //Settings
            val settingsCard: CardView = findViewById(R.id.settingsCard)
            settingsCard.setOnClickListener {
                startActivity(Intent(this@LandingPage, SettingsActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
    }

    override fun onResume() {
        super.onResume()
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this@LandingPage)
                .load(profileImageUrl)
                .placeholder(R.drawable.user_profile)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.user_profile)
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

    @SuppressLint("IntentReset") //
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            pickImageLauncher.launch(intent)
        }
    }

    private fun startPreviewActivity(imageUri: Uri?) {

        if (imageUri != null) {
            val imagePath = getAbsolutePathFromUri(imageUri)
            val intent = Intent(this@LandingPage, PreviewActivity::class.java)
            intent.putExtra(PreviewActivity.EXTRA_IMAGE_PATH, imagePath)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun getAbsolutePathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        } ?: uri.path ?: ""
    }
}