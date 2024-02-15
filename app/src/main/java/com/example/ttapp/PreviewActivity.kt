package com.example.ttapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ExifInterface
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
    }
    private lateinit var previewImageView: ImageView
    private lateinit var btnBack: Button
    private lateinit var btnTranslate: Button

    private lateinit var bitmap: Bitmap
    private lateinit var progressBar: ProgressBar
    private var progressDialog: AlertDialog? = null

    // Create an instance of the OcrHelper class
    private val ocrHelper = OCR()
    // Create an instance of the LanguageRecognizer class
    private val languageRecognizer = LanguageRecognizer()
    // Create an instance of the TextTranslator class
    private val textTranslator = TranslatorHelper(this)
    // Create a variable to store the OCR result
    private lateinit var ocrResultMap: Map<Rect, Text.TextBlock>
    // Create a variable to store the language detected
    private lateinit var languageCode: String
    // Create a variable to store the translated ocr result
    private lateinit var translatedOcrResultMap: Map<Rect, String>
    // Job variable - OCR job
    private lateinit var ocrJob: Job
    // Job variable - language identification job
    private lateinit var languageJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        previewImageView = findViewById(R.id.previewImageView)
        btnBack = findViewById(R.id.btnBack)
        btnTranslate = findViewById(R.id.btnTranslate)

        btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btnTranslate.setOnClickListener {
            startTranslationProcess()
        }

        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)

        progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        Handler(Looper.getMainLooper()).post {
            progressDialog = AlertDialog.Builder(this)
                .setTitle("Processing image...")
                .setCancelable(false)
                .setView(progressBar)
                .show()
        }

        if (imagePath != null) {
            bitmap = readImageFile(imagePath)

            displayBitmap(bitmap)
            showToast("Image not null")

            // Perform OCR in a background thread
            ocrJob = CoroutineScope(Dispatchers.Default).launch {
                ocrResultMap = ocrHelper.performOcr(bitmap)

                withContext(Dispatchers.Main) {
                    // Handle the OCR result
                    processOcrResult(ocrResultMap)
                }
            }

            ocrJob.invokeOnCompletion {
                languageJob = CoroutineScope(Dispatchers.Default).launch {
                    languageCode = languageRecognizer.recognizeLanguage(ocrResultMap)
                    withContext(Dispatchers.Main) {
                        // Handle the language identification result here
                        processLanguageResult(languageCode)
                    }
                }

                languageJob.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Default).launch {
                        translatedOcrResultMap = textTranslator.translateOcrResult(ocrResultMap, languageCode)

                        withContext(Dispatchers.Main) {
                            processTranslationResult(translatedOcrResultMap)
                        }
                    }
                }
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }
    private fun processOcrResult(ocrResultMap: Map<Rect, Text.TextBlock>) {
        // Log the OCR result with Rect and text
        for ((rect, textBlock) in ocrResultMap) {
            Log.d("OCR", "Found text ${textBlock.text} at $rect")
        }
    }


    private fun serializeOcrResult(ocrResultMap: Map<Rect, Text.TextBlock>): String {
        val wordsList = mutableListOf<String>()

        for ((_, textBlock) in ocrResultMap) {
            val words = textBlock.text.split("\\s+".toRegex())
            wordsList.addAll(words)
        }

        // Join the words into a single string
        return wordsList.joinToString(" ")
    }

    private fun startTranslationProcess() {
        if (::ocrResultMap.isInitialized && ::languageCode.isInitialized) {
            val ocrResult =  serializeOcrResult(ocrResultMap) //Gson().toJson(ocrResultMap)

            // Pass OCR result and language code to TextTranslator
            val intent = Intent(this@PreviewActivity, TextTranslator::class.java)
            intent.putExtra("OCR_RESULT", ocrResult)
            intent.putExtra("LANGUAGE_CODE", languageCode)
            Log.d("Inside if", ocrResult)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    else {
            showToast("OCR result or language code not initialized.")
        }
    }

    private fun processLanguageResult(languageResult: String) {
        // Handle the language identification result
        Log.d("Language", "Language detected is $languageResult")

        runOnUiThread {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    private fun processTranslationResult(translatedText: Map<Rect, String>) {
        // Handle the translation result
        for ((rect, text) in translatedText) {
            Log.d("Translation", "Translated text $text at $rect")
        }

        // Get annotated bitmap
        bitmap = BitmapAnnotator.annotateBitmap(bitmap, ocrResultMap, translatedText)
        displayBitmap(bitmap)

        runOnUiThread {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    private fun readImageFile(imagePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(imagePath, options)
        // Rotate the bitmap if required and return it
        return rotateBitmap(imagePath, bitmap)
    }

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

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}