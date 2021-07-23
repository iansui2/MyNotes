package com.iansui.mynotes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDAO {

    @Insert
    suspend fun insert(note: Note)

    @Query("UPDATE note SET title = :title, description = :description, color = :color WHERE noteId =:key")
    suspend fun updateNote(key: Int, title: String, description: String, color: String)

    @Query("UPDATE note SET category =:category WHERE category = (SELECT category FROM note WHERE noteId =:key) ")
    suspend fun updateCategory(key: Int, category: String)

    @Query("DELETE FROM note WHERE noteId = :key")
    suspend fun deleteNote(key: Int)

    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM note ORDER BY noteId ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT DISTINCT category FROM note")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT * FROM note WHERE category= :category ORDER BY noteId ASC")
    fun getCategory(category: String): LiveData<List<Note>>

}