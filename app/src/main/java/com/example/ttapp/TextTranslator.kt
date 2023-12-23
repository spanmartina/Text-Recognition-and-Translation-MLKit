package com.example.ttapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
//import com.google.mlkit.nl.languageid.LanguageIdentification
//import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
//import com.google.mlkit.nl.languageid.LanguageIdentifier

//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translation
////import com.google.mlkit.nl.translate.TranslationOptions
//import com.google.mlkit.nl.translate.Translator

////////**********8
// Import coroutines dependencies
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class TextTranslator : AppCompatActivity() {
    class TextTranslator : AppCompatActivity() {

    //Detect Language initialization
    private lateinit var sourceLanguage: TextInputEditText
//    private lateinit var btnCheckNow: Button
    private lateinit var result: TextView

    //Translate Language initialization
//    private lateinit var targetLanguage: TextView
    private lateinit var btnTranslate: MaterialButton
//    private lateinit var translationResult: TextView
    private lateinit var translationResult: TextInputEditText
    private lateinit var translatedText: String
    // Job variable to keep track of the translation job
    private lateinit var translationJob: Job
    //initialize TranslatorHelper
    private val translatorHelper = TranslatorHelper(this)
    //Initialize the language detection
    private val languageRecognizer = LanguageRecognizer()

//    private lateinit var translatorHelper: TranslatorHelper()
    // Job variable to keep track of the language identification job
//    private lateinit var languageJob: Job

    // Create a variable to store the language detected
//    private lateinit var languageCode: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_translator)

        //Detect Language
        sourceLanguage = findViewById(R.id.sourceLanguage)
//        btnCheckNow = findViewById(R.id.btnCheckNow)
//        result = findViewById(R.id.result)

        //Translate Language
//        targetLanguage = findViewById(R.id.targetLanguage)
        btnTranslate = findViewById(R.id.btnTranslate)
        translationResult = findViewById(R.id.translationResult)

//        btnCheckNow.setOnClickListener {
//            val langText: String = sourceLanguage.text.toString()
//
//            if (langText.isEmpty()) {
//                showToast("Please enter text")
//            } else {
//                detectLang(langText)
//            }
//        }

        val ocrResult = intent.getStringExtra("OCR_RESULT")
        sourceLanguage.setText(ocrResult)

        btnTranslate.setOnClickListener {
            val langText: String = sourceLanguage.text.toString()

            if (langText.isEmpty()) {
                showToast("Please enter text")
            } else {
                translateText(langText)
//                val detectedLanguageCode: String = result.text.toString()
//
//                if (detectedLanguageCode == "und") {
//                    showToast("Cannot translate unidentified language")
//                } else {
//                    // Detect language and translate
//                    languageJob = CoroutineScope(Dispatchers.Default).launch {
//                        translatedText =
//                            translatorHelper.detectAndTranslate(langText, detectedLanguageCode)
//                        withContext(Dispatchers.Main) {
//                            translationResult.text = "Translated Text: $translatedText"
//                        }
//                    }
//                }
            }
        }

    }

//    @SuppressLint("SetTextI18n")
//    private fun detectLang(langText: String){
//        val languageIdentifier:LanguageIdentifier=LanguageIdentification.getClient()
//
//        languageIdentifier.identifyLanguage(langText)
//            .addOnSuccessListener { languageCode ->
//                if (languageCode == "und") {
//                    result.text="Can't identify language"
//                } else {
//                    result.text= languageCode
//                }
//            }
//            .addOnFailureListener {
//                result.text="Exception ${it.message}"
//            }
//
//    }
    @SuppressLint("SetTextI18n")
    private fun translateText(langText: String) {
        translationJob = CoroutineScope(Dispatchers.Default).launch {
            // Use the LanguageRecognizer to identify the language
            val detectedLanguageCode = languageRecognizer.detectLanguage(langText)

//            withContext(Dispatchers.Main) {
//                if (detectedLanguageCode == "und") {
//                    showToast("Can't identify language")
//                } else {
//                    translationResult.setText(detectedLanguageCode)
//                }
//            }

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
}