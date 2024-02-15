package com.example.ttapp

import android.app.Activity
import com.google.android.gms.tasks.Tasks
import android.content.Context
import android.graphics.Rect
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.os.Handler
import android.os.Looper
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.vision.text.Text

class TranslatorHelper(private val context: Context) {

    private var  sourceOptions: TranslatorOptions
    private var sourceTranslator: com.google.mlkit.nl.translate.Translator
    private val remoteModelManager = RemoteModelManager.getInstance()

    // AlertDialog for download progress
    private var progressDialog: AlertDialog? = null
    init {
        // Initialize the source (spanish) translator
        sourceOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag("es").toString())  // Default source language (will be dynamically changed)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        sourceTranslator = com.google.mlkit.nl.translate.Translation.getClient(sourceOptions)
    }

    // sourceLanguageCode is the language code in BCP-47 format, e.g., "es", "de", "fr", etc.
    private fun translateTextToEnglish(text: String, sourceLanguageCode: String): String {
        if (sourceLanguageCode == "und") {
            return text
        }
        // Check if the translation model is downloaded and available
        if (!isModelDownloaded(sourceLanguageCode)) {
            // Model not downloaded, download it and wait for completion
            downloadModel(sourceLanguageCode)
        }
        // Set the source language dynamically
        sourceOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLanguageCode).toString())
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        // Initialize the translator with the updated options
        sourceTranslator = com.google.mlkit.nl.translate.Translation.getClient(sourceOptions)

        // Translate text
        val task = sourceTranslator.translate(text)
        return Tasks.await(task)
    }

    // Check if the translation model for the given language code is downloaded and available
    private fun isModelDownloaded(languageCode: String): Boolean {
        val model = TranslateRemoteModel.Builder(languageCode).build()
        val task = remoteModelManager.isModelDownloaded(model)
        return Tasks.await(task)
    }

    // Download translation model for the given language code
    private fun downloadModel(languageCode: String) {
        val progressBar = ProgressBar(context).apply {
            isIndeterminate = true
        }

        Handler(Looper.getMainLooper()).post {
            progressDialog = AlertDialog.Builder(context)
                .setTitle("Downloading Translation Model (Requires Wi-Fi connection)")
                .setCancelable(false)
                .setView(progressBar)
                .show()
        }

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        val model = TranslateRemoteModel.Builder(languageCode).build()
        val downloadTask = remoteModelManager.download(model, conditions)

        try {
            Tasks.await(downloadTask)
            Handler(Looper.getMainLooper()).post {
                progressDialog?.dismiss()
                progressDialog = null
            }
            showDownloadToast("Translation Model Downloaded Successfully")
        } catch (e: Exception) {
            // Dismiss the progress dialog on download failure
            Handler(Looper.getMainLooper()).post {
                progressDialog?.dismiss()
                progressDialog = null
            }
            showDownloadToast("Translation Model Download Failed")
        }
    }

    private fun showDownloadToast(message: String) {
        (context as? Activity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun detectAndTranslate(text: String, languageCode: String ) : String{
        return try {
            translateTextToEnglish(text, languageCode)
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }

    fun translateOcrResult(ocrResult: Map<Rect, Text.TextBlock>, languageCode: String ): Map<Rect, String> {
        // Create a map to store the translated result
        val translatedResult = mutableMapOf<Rect, String>()

        // Iterate through the ocr result
        for ((rect, textBlock) in ocrResult) {
            // Translate the textBlock text to english
            val translatedText = translateTextToEnglish(textBlock.text, languageCode)
            // Add the translated text to the map
            translatedResult[rect] = translatedText
        }
        return translatedResult
    }
}