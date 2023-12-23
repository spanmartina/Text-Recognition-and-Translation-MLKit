package com.example.ttapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import android.content.Intent
import android.widget.ImageView

class LandingPage : AppCompatActivity() {


    private lateinit var btnUpload: MaterialButton
    private lateinit var btnCapture: MaterialButton
    private lateinit var imageContainer: ImageView
    private lateinit var btnDetectText: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Initialize views
        btnUpload = findViewById(R.id.btnUpload)
        btnCapture = findViewById(R.id.btnCapture)
        imageContainer = findViewById(R.id.imageContainer)
        btnDetectText = findViewById(R.id.btnDetectText)

        // Set click listener for the Upload button
        btnUpload.setOnClickListener {
//            openGallery()
        }

        // Set click listener for the Capture button
        btnCapture.setOnClickListener {
            // Open CameraActivity when the "Capture" button is pressed
            val intent = Intent(this@LandingPage, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}