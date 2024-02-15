package com.example.ttapp

import android.graphics.Rect
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.vision.text.Text
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class LanguageRecognizer {
    private val languageIdentifierClient: LanguageIdentifier
    private val languageIdentifierOptions: LanguageIdentificationOptions =
        LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()

    init {
        // Initialize the language identifier client in the class constructor
        //confidence threshold of 0.5
        languageIdentifierClient = LanguageIdentification.getClient(languageIdentifierOptions)
    }

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

    fun detectLanguage(text: String): String {
        // Create a task to recognize the language of the text
        val task: Task<String> = languageIdentifierClient.identifyLanguage(text)

        return try {
            Tasks.await(task)
        } catch (e: Exception) {
            "und"
        }
    }
}