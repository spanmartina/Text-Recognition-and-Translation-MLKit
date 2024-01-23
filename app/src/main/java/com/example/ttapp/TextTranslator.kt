package com.example.ttapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.ttapp.databinding.ActivityTextTranslatorBinding

class TextTranslator : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTextTranslatorBinding
    private lateinit var sourceLanguageLabel: TextView
    private lateinit var targetLanguageLabel: TextView

    //Detect Language initialization
    private lateinit var sourceLanguage: TextInputEditText
    private lateinit var btnTranslate: Button
    private lateinit var translationResult: TextInputEditText
    private lateinit var translatedText: String
    // Job variable to keep track of the translation job
    private lateinit var translationJob: Job
    //Backup btn
    private lateinit var btnBackup: FloatingActionButton

    //initialize TranslatorHelper
    private val translatorHelper = TranslatorHelper(this)
    //Initialize the language detection
    private val languageRecognizer = LanguageRecognizer()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_text_translator)

        //Data binding
        viewBinding = ActivityTextTranslatorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        sourceLanguageLabel = findViewById(R.id.sourceLanguageLabel)
        targetLanguageLabel = findViewById(R.id.targetLanguageLabel)
        //Detect Language
        sourceLanguage = findViewById(R.id.sourceLanguage)

        //Translate Language
        btnTranslate = findViewById(R.id.btnTranslate)
        translationResult = findViewById(R.id.translationResult)
        btnBackup = findViewById(R.id.btnBackup)

//        val BACKUP_REQUEST_CODE = 1

        //Bottom Nav
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_translator


        val ocrResult = intent.getStringExtra("OCR_RESULT")
        sourceLanguage.setText(ocrResult)

        if (!ocrResult.isNullOrEmpty()) {
            // Call translateText with the provided OCR result
            Log.d("??? YES OCR", "No OCR result available.")
            translateText(ocrResult)
        } else {
            showToast("No OCR result available.")
            Log.d("??? NO OCR", "No OCR result available.")
        }

        btnTranslate.setOnClickListener {
            val langText: String = sourceLanguage.text.toString()

            if (langText.isEmpty()) {
                showToast("Please enter text")
            } else {
                translateText(langText)
            }
        }

        viewBinding.btnBackup.setOnClickListener {
            Log.d("BACKUP","??? btm backup clicked.")
            val intent = Intent(this, BackupActivity::class.java)

            // place data in intent
            intent.putExtra("sourceLang", sourceLanguageLabel.text.toString())
            intent.putExtra("targetLang", targetLanguageLabel.text.toString())
            intent.putExtra("sourceText", sourceLanguage.text.toString())
            intent.putExtra("targetText", translationResult.text.toString())

//            startActivityForResult(intent, BACKUP_REQUEST_CODE)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        //Bottom Nav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_translator -> true
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, LandingPage::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_ocr -> {
                    startActivity(Intent(applicationContext, CameraActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun translateText(langText: String) {
        translationJob = CoroutineScope(Dispatchers.Default).launch {
            val detectedLanguageCode = languageRecognizer.detectLanguage(langText)
            Log.d("??? inside detectedLanguageCode", "Language code: $detectedLanguageCode")

            withContext(Dispatchers.Main) {
                if (detectedLanguageCode == "und") {
                    showToast("Can't identify language")
                } else {
                    // Get language name from language code
                    val languageName = getLanguageNameFromCode(detectedLanguageCode)
                    Log.d("??? languageName", " $languageName")
                    // Set the detected language to the TextView
                    sourceLanguageLabel.text = languageName
                }
            }
            // Translate to English
            translatedText = translatorHelper.detectAndTranslate(langText, detectedLanguageCode)

            withContext(Dispatchers.Main) {
                translationResult.setText(translatedText)
            }
        }
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun getLanguageNameFromCode(languageCode: String): String {
        val locale = Locale(languageCode)
        return locale.displayLanguage
    }
}