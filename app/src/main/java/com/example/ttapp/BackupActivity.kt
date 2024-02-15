package com.example.ttapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BackupActivity : AppCompatActivity() {

    private lateinit var fileName: EditText
    private lateinit var sourceLang: EditText
    private lateinit var targetLang: EditText
    private lateinit var sourceText: EditText
    private lateinit var targetText: EditText
    private lateinit var btnBackup: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        fileName = findViewById(R.id.fileName)
        sourceLang = findViewById(R.id.sourceLang)
        targetLang = findViewById(R.id.targetLang)
        sourceText = findViewById(R.id.sourceText)
        targetText = findViewById(R.id.targetText)
        btnBackup = findViewById(R.id.btnBackup)

        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("users")
        auth = FirebaseAuth.getInstance()

        // Retrieve data from Intent
        val intentData = intent.extras
        if (intentData != null) {
            val sourceLangExtra = intentData.getString("sourceLang")
            val targetLangExtra = intentData.getString("targetLang")
            val sourceTextExtra = intentData.getString("sourceText")
            val targetTextExtra = intentData.getString("targetText")

            sourceLang.setText(sourceLangExtra)
            targetLang.setText(targetLangExtra)
            sourceText.setText(sourceTextExtra)
            targetText.setText(targetTextExtra)
        }

        btnBackup.setOnClickListener {
            val fileName = fileName.text.toString()
            val sourceLanguage = sourceLang.text.toString()
            val targetLanguage = targetLang.text.toString()
            val sourceText = sourceText.text.toString()
            val targetText = targetText.text.toString()

            val currentUserUid = auth.currentUser?.uid
            if (currentUserUid != null) {
                // Create a Note object
                val note = SavedNotes(
                    fileName,
                    sourceLanguage,
                    targetLanguage,
                    sourceText,
                    targetText,
                )
                // Save the note to the specific user's notes
                reference.child(currentUserUid).child("notes").push().setValue(note)
                finish()
            }
        }
    }
}