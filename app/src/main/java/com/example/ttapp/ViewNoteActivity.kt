package com.example.ttapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.ttapp.databinding.ActivityViewNoteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewNoteBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("users")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_translator

        val fileName = intent.getStringExtra("FileName")
        val sourceLang = intent.getStringExtra("SourceLang")
        val targetLang = intent.getStringExtra("TargetLang")
        val sourceText = intent.getStringExtra("SourceText")
        val targetText = intent.getStringExtra("TargetText")

        binding.fileName.text = Editable.Factory.getInstance().newEditable(fileName)
        binding.sourceLang.text = Editable.Factory.getInstance().newEditable(sourceLang)
        binding.targetLang.text = Editable.Factory.getInstance().newEditable(targetLang)
        binding.sourceText.text = Editable.Factory.getInstance().newEditable(sourceText)
        binding.targetText.text = Editable.Factory.getInstance().newEditable(targetText)
        binding.checkIcon.setOnClickListener {
            finish()
        }

        //Bottom Nav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, LandingPage::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_ocr -> {
                    startActivity(Intent(applicationContext, CameraActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_translator -> {
                    startActivity(Intent(applicationContext, TextTranslator::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}