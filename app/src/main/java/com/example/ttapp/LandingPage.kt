package com.example.ttapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView

    class LandingPage : AppCompatActivity() {
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
        }

        //TODO
        //Saved
//        val savedCard: CardView = findViewById(R.id.savedCard)
//        savedCard.setOnClickListener {
//            val intent = Intent(this@LandingPage, BackupActivity::class.java)
//            startActivity(intent)
//        }
        //Settings
//        val settingsCard: CardView = findViewById(R.id.settingsCard)
//        settingsCard.setOnClickListener {
//            val intent = Intent(this@LandingPage, SettingsActivity::class.java)
//            startActivity(intent)
//        }

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
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
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
        }
//        else {
            // Handle the case where imageUri is null
//            showToast("Selected image URI is null", Toast.LENGTH_SHORT).show()
//        }
    }
}