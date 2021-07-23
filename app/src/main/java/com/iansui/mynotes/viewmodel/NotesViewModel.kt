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

    private val _color = MutableLiveData<String>()
    val color: LiveData<String> get() = _color

    val notes = noteRepository.getAllNotes()

    val categories = noteRepository.getAllCategories()

    init {
        _color.value = "light_yellow"
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setColor(color: String) {
        _color.value = color
    }

    private fun insert(note: Note) {
        noteRepository.insert(note)
    }

    fun getCategory(category: String): LiveData<List<Note>> {
        return noteRepository.getCategory(category)
    }

    fun onSaveNote() {
        val newNote = Note(title = _title.value!!, description = _description.value!!, category = _category.value!!, color = _color.value!!)
        insert(newNote)
    }

    fun onUpdateNote(id: Int) {
        noteRepository.updateNote(id, _title.value!!, _description.value!!, _color.value!!)
        noteRepository.updateCategory(id, _category.value!!)
    }

    fun onDeleteNote(id: Int) {
        noteRepository.deleteById(id)
    }

    fun onDeleteAllNotes() {
        noteRepository.deleteAll()
    }
}