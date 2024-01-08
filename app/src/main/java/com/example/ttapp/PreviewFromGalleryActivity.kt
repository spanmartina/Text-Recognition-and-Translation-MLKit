package com.example.ttapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.Job
import java.io.FileNotFoundException
import java.io.IOException

class PreviewFromGalleryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
//    val selectedImageUri: Uri? = intent.getParcelableExtra<Uri>(PreviewFromGalleryActivity.EXTRA_IMAGE_URI)
//    val selectedImageUri: Uri? = intent.getStringExtra(EXTRA_IMAGE_URI)

    private lateinit var previewImageView: ImageView
    private lateinit var btnBack: Button
    private lateinit var btnTranslate: Button

    private var bitmap: Bitmap? = null
    // Create a progress dialog
    private lateinit var progressBar: ProgressBar

    // AlertDialog to show download progress
    private var progressDialog: AlertDialog? = null
    // Create a variable to store the OCR result
    private lateinit var ocrResultMap: Map<Rect, Text.TextBlock>

    // Create a variable to store the language detected
    private lateinit var languageCode: String
    // Create a variable to store the translated ocr result
    private lateinit var translatedOcrResultMap: Map<Rect, String>

    // Job variable to keep track of the OCR job
    private lateinit var ocrJob: Job

    // Job variable to keep track of the language identification job
    private lateinit var languageJob: Job

    //***************
    // Create an instance of the OcrHelper class
    private val ocrHelper = OCR()

    // Create an instance of the LanguageRecognizer class
    private val languageRecognizer = LanguageRecognizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_from_gallery)


        previewImageView = findViewById(R.id.previewImageView)
        btnBack = findViewById(R.id.btnBack)
        btnTranslate = findViewById(R.id.btnTranslate)

        btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btnTranslate.setOnClickListener {
            // Handle the translation button click
            // Perform translation logic here
            // For example, you can call a function to start the translation process
            startTranslationProcess()
        }
        val imagePath = intent.getStringExtra(PreviewActivity.EXTRA_IMAGE_PATH)
//        val selectedImageUri = intent.data
        val selectedImageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val selectedImageUri : Uri? =intent.getParcelableExtra(EXTRA_IMAGE_URI)
//        val selectedImageUri = intent.getStringExtra(PreviewActivity.EXTRA_IMAGE_PATH)

        // Initialize the progress bar
        progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        // Show the progress dialog saying that translation is in progress
        Handler(Looper.getMainLooper()).post {
            progressDialog = AlertDialog.Builder(this)
                .setTitle("Processing image...")
                .setCancelable(false)
                .setView(progressBar)
                .show()
        }
        Log.e("Outside URI!!!", selectedImageUri.toString())
//        Log.e("ImagePath!!!", imagePath.toString())
        if (selectedImageUri != null) {
//            if (selectedImageUriString != null) {
            Log.e("URI!!!", selectedImageUri.toString())
            bitmap = getBitmapFromUri(selectedImageUri)

            if (bitmap != null) {
                displayBitmap(bitmap!!)
                showToast("Gallery Image not null")

                // For now, let's just dismiss the progress dialog
                Handler(Looper.getMainLooper()).post {
                    progressDialog?.dismiss()
                    progressDialog = null
                }
            } else {
                showToast("Failed to load bitmap from selected image URI.")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            showToast("Selected image URI is null.")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }


    // Create a function to process the OCR result
    private fun processOcrResult(ocrResultMap: Map<Rect, Text.TextBlock>) {
        // Log the OCR result with Rect and text
        for ((rect, textBlock) in ocrResultMap) {
            Log.d("OCR", "Found text ${textBlock.text} at $rect")

        }

    }

    // Create a function to process the translation result
//    private fun processTranslationResult(translatedText: Map<Rect, String>) {
//        // Handle the translation result
//        for ((rect, text) in translatedText) {
//            Log.d("Translation", "Translated text $text at $rect")
//        }
//
//        // Get annotated bitmap
//        bitmap = BitmapAnnotator.annotateBitmap(bitmap, ocrResultMap, translatedText)
//
//        // Display the annotated bitmap
//        displayBitmap(bitmap)
//
//        // Dismiss the progress dialog
//        Handler(Looper.getMainLooper()).post {
//            progressDialog?.dismiss()
//            progressDialog = null
//        }
//
//    }
    private fun serializeOcrResult(ocrResultMap: Map<Rect, Text.TextBlock>): String {
//        val serializedResult = ocrResultMap.mapValues { (_, textBlock) ->
//            textBlock.text
//        }
//        return Gson().toJson(serializedResult)

        val wordsList = mutableListOf<String>()

        for ((_, textBlock) in ocrResultMap) {
            // Split the text into words and add them to the list
            val words = textBlock.text.split("\\s+".toRegex())
            wordsList.addAll(words)
        }

        // Join the words into a single string
        return wordsList.joinToString(" ")
    }

    //Start translation process

    private fun startTranslationProcess() {
//        Log.d("Outside if", "Language detected is $languageCode")

        if (::ocrResultMap.isInitialized && ::languageCode.isInitialized) {
//             Convert the Map to JSON


            val ocrResult =  serializeOcrResult(ocrResultMap) //Gson().toJson(ocrResultMap)

            // Pass OCR result and language code to TextTranslator
            val intent = Intent(this@PreviewFromGalleryActivity, TextTranslator::class.java)
            intent.putExtra("OCR_RESULT", ocrResult)
            intent.putExtra("LANGUAGE_CODE", languageCode)
            Log.d("Inside if", ocrResult)
            startActivity(intent)
        }
        else {
            showToast("OCR result or language code not initialized.")
        }


    }

    // Create a function to process the language identification result
    private fun processLanguageResult(languageResult: String) {
        // Handle the language identification result
        Log.d("Language", "Language detected is $languageCode")


        // Dismiss the progress dialog
        Handler(Looper.getMainLooper()).post {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }
    // Create a function to read image file and return bitmap
    /* PREVIOUS CODE
    private fun readImageFile(imagePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(imagePath, options)
        // Rotate the bitmap if required and return it
        return rotateBitmap(imagePath, bitmap)
    }
     */

    private fun readImageFile(filePath: String?): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                // Set inSampleSize to decode a scaled-down version of the image
                inSampleSize = 4
            }

            // Decode the bitmap from the file path
            BitmapFactory.decodeFile(filePath, options)
        } catch (e: FileNotFoundException) {
            // Handle the case where the file is not found
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // Handle other exceptions during decoding
            e.printStackTrace()
            null
        }
    }

    // Create a function to display the bitmap
    private fun displayBitmap(bitmap: Bitmap) {
        previewImageView.setImageBitmap(bitmap)
    }

    private fun rotateBitmap(imagePath: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        return if (rotationDegrees != 0) {
            val matrix = android.graphics.Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: run {
                Log.e("PreviewActivity", "Input stream is null for URI: $uri")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PreviewActivity", "Error decoding bitmap from URI: $uri", e)
            null
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}