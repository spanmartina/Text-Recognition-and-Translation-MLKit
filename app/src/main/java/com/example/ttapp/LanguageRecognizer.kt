package com.example.ttapp

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.text.Text
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import android.text.TextUtils


class LanguageRecognizer {

    private val languageIdentifierClient: LanguageIdentifier
    private val languageIdentifierOptions: LanguageIdentificationOptions

    init {
        // Initialize the language identifier client in the class constructor
        //specifying a confidence threshold of 0.5
        languageIdentifierOptions = LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
        languageIdentifierClient = LanguageIdentification.getClient(languageIdentifierOptions)
    }


    //Text.TextBlock objects representing the recognized text blocks
//    The function returns a String representing the identified language.

//    fun recognizeLanguage(ocrMap: Map<Rect, Text.TextBlock>): String {
//        for ((_, textBlock) in ocrMap) {
//            val text = textBlock.text
//            val task: Task<String> = languageIdentifierClient.identifyLanguage(text)
//            val result = Tasks.await(task)
//            if (result.isNotBlank()) {
//                return result
//            }
//        }
//        return "Language NOT identified";
//    }

    fun recognizeLanguage(ocrMap: Map<Rect, Text.TextBlock>): String {

        val languageMap = mutableMapOf<String, Int>()

        // Iterate through the map of OCR results
        for ((_, textBlock) in ocrMap) {
            // Get the text from the textBlock
            val text = textBlock.text

            // Create a task to recognize the language of the textBlock
            val task: Task<String> = languageIdentifierClient.identifyLanguage(text)

            // Wait for the task to complete
            val result = Tasks.await(task)

            // Store the language in the map
            languageMap[result] = (languageMap[result] ?: 0) + 1
        }
        val mostCommonLanguage = languageMap.maxByOrNull { it.value }?.key


        // Return the most common language or "und" if none is found
        return mostCommonLanguage ?: "und"



    }

    //detect language from string
    fun detectLanguage(text: String): String {
        // Create a task to recognize the language of the text
        val task: Task<String> = languageIdentifierClient.identifyLanguage(text)

        // Wait for the task to complete
        return try {
            Tasks.await(task)
        } catch (e: Exception) {
            "und" // Return "und" (undetermined) if language identification fails
        }
    }

    //Recognize text EditText
    fun languageRecognizer(inputText: String): String? {
        if (TextUtils.isEmpty(inputText)) {
//            return null
            return "Language NOT identified"
        }

        val task: Task<String> = languageIdentifierClient.identifyLanguage(inputText)
        return try {
            Tasks.await(task)
        } catch (e: Exception) {
            e.printStackTrace()
//            null
            "Language NOT identified"
        }
    }
}