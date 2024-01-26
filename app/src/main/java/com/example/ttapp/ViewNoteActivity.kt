package com.example.ttapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.ttapp.databinding.ActivityViewNoteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        //Bottom Nav
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
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
//            updateData(fileName, sourceLang, targetLang, sourceText, targetText)
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

//    private fun updateData(fileName: String?, sourceLang: String?, targetLang: String?, sourceText: String?, targetText: String?) {
//        val currentUserUid = auth.currentUser?.uid
//
//        if (currentUserUid != null && fileName != null && sourceLang != null && targetLang != null) {
//            // Fetch the note based on unique attributes (e.g., fileName, sourceLang, targetLang)
//            reference.child(currentUserUid).child("notes")
//                .orderByChild("fileName")
//                .equalTo(fileName)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (noteSnapshot in snapshot.children) {
//                            // Update the data in the Firebase database
//                            val updatedData = HashMap<String, Any>()
//                            updatedData["sourceLanguage"] = binding.sourceLang.text.toString()
//                            updatedData["targetLanguage"] = binding.targetLang.text.toString()
//                            updatedData["sourceText"] = binding.sourceText.text.toString()
//                            updatedData["targetText"] = binding.targetText.text.toString()
//
//                            noteSnapshot.ref.updateChildren(updatedData)
////                            Toast.makeText(this@ViewNoteActivity, "Data updated successfully", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
////                    override fun onCancelled(error: DatabaseError) {
////                        Toast.makeText(this@ViewNoteActivity, "Error fetching note", Toast.LENGTH_SHORT).show()
////                    }
//                })
//        }
//    }
}