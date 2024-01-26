package com.example.ttapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ttapp.databinding.ActivitySavedNotesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SavedNotesActivity : AppCompatActivity() {
    private var reference: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var eventListener: ValueEventListener? = null
    private lateinit var dataList: ArrayList<SavedNotes>
//    private lateinit var keysList: ArrayList<SavedNotes>

    private lateinit var adapter: NotesAdaptor
    private lateinit var binding: ActivitySavedNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //Bottom Nav
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_translator


        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            reference = database.reference.child("users").child(currentUserUid).child("notes")
            Log.d("@reference", reference.toString())
        }

        val gridLayoutManager = GridLayoutManager(this@SavedNotesActivity, 1)
        binding.notesView.layoutManager = gridLayoutManager
        binding.search.clearFocus()
        val builder = AlertDialog.Builder(this@SavedNotesActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
//        keysList = ArrayList()

        adapter = NotesAdaptor(this@SavedNotesActivity, dataList)
        binding.notesView.adapter = adapter
        dialog.show()

        eventListener = reference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(SavedNotes::class.java)
                    if (dataClass != null ) {
                        dataList.add(dataClass)
                    }
                }

                // Reverse the order of dataList
//                dataList = dataList.reversed() as ArrayList<SavedNotes>
                adapter.notifyDataSetChanged()
//                adapter.setSearchDataList(dataList)
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

        binding.btnAdd.setOnClickListener{
            val intent = Intent(this@SavedNotesActivity, BackupActivity::class.java)
            startActivity(intent)
            finish()
            adapter.notifyDataSetChanged()
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

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

//        adapter.setOnItemClickListener(object : NotesAdaptor.OnItemClickListener {
//            override fun onDeleteClick(position: Int, key: String) {
//                showDeleteDialog(position, key)
//            }
//        })
    }
    fun searchList(text: String) {
        val searchList = java.util.ArrayList<SavedNotes>()
        for (dataClass in dataList) {
            if (dataClass.fileName?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

//    private fun showDeleteDialog(position: Int, key: String) {
//        val alertDialogBuilder = AlertDialog.Builder(this)
//        alertDialogBuilder.setTitle("Delete Note")
//        alertDialogBuilder.setMessage("Are you sure you want to delete this note?")
//        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
//            // Delete note from the database and update UI
////            deleteNote(position)
////            Log.d("@position", dataList[position].key().toString())
//            Log.d("@position", position.toString())
//            Log.d("@key", key)
//
//            val intent = Intent(this@SavedNotesActivity, SavedNotesActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
//            dialog.dismiss()
//        }
//        alertDialogBuilder.create().show()
//    }

//    private fun deleteNote(position: Int) {
//        // Delete note from the database
//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
//
//        if (currentUserUid != null) {
//            val noteRef = FirebaseDatabase.getInstance().getReference("users/$currentUserUid/notes/")
//            noteRef.child(dataList[position].key!!).removeValue()
//
//            // Remove the note from the local list and notify the adapter
//            dataList.removeAt(position)
//            adapter.notifyItemRemoved(position)
//        }
//    }
}