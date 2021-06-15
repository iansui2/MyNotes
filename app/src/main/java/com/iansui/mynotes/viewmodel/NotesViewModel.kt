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

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    val notes = noteRepository.getAllNotes()

    val categories = noteRepository.getAllCategories()

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    private fun insert(note: Note) {
        noteRepository.insert(note)
    }

    fun onSaveNote() {
        val newNote = Note(title = _title.value!!, description = _description.value!!, category = _category.value!!)
        insert(newNote)
    }

    fun onUpdateNote(id: Int) {
        noteRepository.updateNote(id, _title.value!!, _description.value!!)
        noteRepository.updateCategory(id, _category.value!!)
    }

    fun onDeleteNote(id: Int) {
        noteRepository.deleteById(id)
    }

    fun onDeleteAllNotes() {
        noteRepository.deleteAll()
    }
}