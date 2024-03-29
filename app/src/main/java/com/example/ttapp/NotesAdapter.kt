package com.example.ttapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NotesAdaptor(
    private val context: Context,
    private var dataList: List<SavedNotes>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = dataList[position]
        holder.fileName.text = note.fileName
        holder.fileContent.text = "${note.sourceLanguage} - ${note.targetLanguage}"

        holder.cardNote.setOnClickListener {
            val intent = Intent(context, ViewNoteActivity::class.java)
            intent.putExtra("FileName", note.fileName)
            intent.putExtra("SourceLang", note.sourceLanguage)
            intent.putExtra("TargetLang", note.targetLanguage)
            intent.putExtra("SourceText", note.sourceText)
            intent.putExtra("TargetText", note.targetText)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchDataList(searchList: List<SavedNotes>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var fileName: TextView
    var fileContent: TextView
    var cardNote: CardView

    init {
        fileName = itemView.findViewById(R.id.fileName)
        fileContent = itemView.findViewById(R.id.fileContent)
        cardNote = itemView.findViewById(R.id.cardNote)
    }
}