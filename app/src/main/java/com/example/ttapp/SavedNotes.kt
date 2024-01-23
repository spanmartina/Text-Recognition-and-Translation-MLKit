package com.example.ttapp

class SavedNotes {
    var fileName: String? = null
    var sourceLanguage: String? = null
    var targetLanguage: String? = null
    var sourceText: String? = null
    var targetText: String? = null

    constructor( fileName: String?, sourceLanguage: String?, targetLanguage: String?, sourceText: String?, targetText: String?) {
        this.fileName = fileName
        this.sourceLanguage = sourceLanguage
        this.targetLanguage = targetLanguage
        this.sourceText = sourceText
        this.targetText = targetText
    }
}