package com.iansui.mynotes.repository

import androidx.lifecycle.LiveData
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.database.NotesDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesRepository (private val notesDAO: NotesDAO) {

    fun insert(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.insert(note)
        }
    }

    fun updateNote(id: Int, title: String, description: String, color: String) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.updateNote(id, title, description, color)
        }
    }

    fun updateCategory(id: Int, category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.updateCategory(id, category)
        }
    }

    fun deleteById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.deleteNote(id)
        }
    }

    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.deleteAllNotes()
        }
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return notesDAO.getAllNotes()
    }

    fun getAllCategories(): LiveData<List<String>> {
        return notesDAO.getAllCategories()
    }

    fun getCategory(category: String): LiveData<List<Note>> {
        return notesDAO.getCategory(category)
    }
}