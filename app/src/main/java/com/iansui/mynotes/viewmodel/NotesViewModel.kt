package com.iansui.mynotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.repository.NotesRepository

class NotesViewModel(
        private val noteRepository: NotesRepository
): ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    val notes = noteRepository.getAllNotes()

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    private fun insert(note: Note) {
        noteRepository.insert(note)
    }

    private fun update(note: Note) {
        noteRepository.update(note)
    }

    fun onSaveNote() {
        val newNote = Note(title = _title.value!!, description = _description.value!!)
        insert(newNote)
    }

    fun onUpdateNote(id: Int) {
        val updatedNote = Note(id, _title.value!!, _description.value!!)
        update(updatedNote)
    }

    fun onDeleteNote(id: Int) {
        noteRepository.deleteById(id)
    }

    fun onDeleteAllNotes() {
        noteRepository.deleteAll()
    }
}