package com.iansui.mynotes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDAO {

    @Insert
    suspend fun insert(note: Note)

    @Query("UPDATE note SET title = :title, description = :description WHERE id =:key")
    suspend fun updateNote(key: Int, title: String, description: String)

    @Query("UPDATE note SET category =:category WHERE category = (SELECT category FROM note WHERE id =:key) ")
    suspend fun updateCategory(key: Int, category: String)

    @Query("DELETE FROM note WHERE id = :key")
    suspend fun deleteNote(key: Int)

    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM note ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT DISTINCT category FROM note ORDER by category ASC")
    fun getAllCategories(): LiveData<List<String>>

}