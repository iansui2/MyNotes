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

    fun update(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDAO.update(note)
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
}