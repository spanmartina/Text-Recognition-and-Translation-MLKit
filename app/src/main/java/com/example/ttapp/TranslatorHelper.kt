package com.example.ttapp

import android.app.Activity
import com.google.android.gms.tasks.Tasks

import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.util.Log

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import java.util.Locale

class TranslatorHelper(private val context: Context) {

    // Variables
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

    // Function to set the source language dynamically
//    fun setSourceLanguage(languageCode: String) {
//        sourceOptions = TranslatorOptions.Builder()
////            .setSourceLanguage(getTranslateLanguageForCode(languageCode))
//            .setSourceLanguage(TranslateLanguage.ITALIAN)
//
//            .setTargetLanguage(TranslateLanguage.ENGLISH)
//            .build()
//
//        sourceTranslator = com.google.mlkit.nl.translate.Translation.getClient(sourceOptions)
//    }


    // sourceLanguageCode is the language code in BCP-47 format, e.g., "es", "de", "fr", etc.
    private fun translateTextToEnglish(text: String, sourceLanguageCode: String): String {
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
        // Create a progress dialog
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
//        setSourceLanguage(languageCode)
        return try {
            Log.d("??? detectAndTranslate", "Language detected: $languageCode is and the text: $text  ")
            translateTextToEnglish(text, languageCode)
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }
}