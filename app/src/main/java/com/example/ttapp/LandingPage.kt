package com.example.ttapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ttapp.databinding.ActivityLandingPageBinding
class LandingPage : AppCompatActivity() {

    private lateinit var viewBinding: ActivityLandingPageBinding
    private lateinit var btnUpload: MaterialButton
    private lateinit var btnCapture: MaterialButton
    private lateinit var imageContainer: ImageView
    private lateinit var btnDetectText: MaterialButton


        //to handle the result of Camera/Gallery permission
//        private  val STORAGE_REQUEST_CODE = 100
    //    private lateinit var storagePermission: Array<String>


    private var imageUri: Uri? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                // Now, you can do something with the selected image URI
                // For example, you might want to display the image or extract text from it
                // ...
                //Start the PreviewActivity with the selected image URI
                startPreviewActivity(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Initialize views
        btnUpload = findViewById(R.id.btnUpload)
        btnCapture = findViewById(R.id.btnCapture)
        imageContainer = findViewById(R.id.imageContainer)
        btnDetectText = findViewById(R.id.btnDetectText)

        //init array of permission required for Storage
//        storagePermission = arrayOf(Manifest.permission.)

        // Set click listener for the Upload button
        btnUpload.setOnClickListener {
            pickImageFromGallery()
        }

        // Set click listener for the Capture button
        btnCapture.setOnClickListener {
            // Open CameraActivity when the "Capture" button is pressed
            val intent = Intent(this@LandingPage, CameraActivity::class.java)
            startActivity(intent)
        }
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