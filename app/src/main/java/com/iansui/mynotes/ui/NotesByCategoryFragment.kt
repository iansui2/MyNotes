package com.iansui.mynotes.ui

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iansui.mynotes.MainActivity
import com.iansui.mynotes.R
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.FragmentNotesByCategoryBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory

class NotesByCategoryFragment : Fragment() {

    private lateinit var binding: FragmentNotesByCategoryBinding
    private lateinit var sharedViewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter
    private lateinit var categoryList: LiveData<List<Note>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentNotesByCategory =
            FragmentNotesByCategoryBinding.inflate(inflater, container, false)
        binding = fragmentNotesByCategory
        return fragmentNotesByCategory.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity: MainActivity = activity as MainActivity
        categoryList = mainActivity.categoryList

        val application: Application = requireNotNull(this.activity).application

        val dataSource = NotesRepository(NotesDatabase.getInstance(application).notesDAO)

        val viewModelFactory = NotesViewModelFactory(dataSource)

        sharedViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NotesViewModel::class.java)

        binding.viewModel = sharedViewModel

        val destination = getString(R.string.NotesByCategoryFragment)

        adapter = NotesAdapter(NotesAdapter.OnClickListener {
            val editNoteNavDirection =
                NotesByCategoryFragmentDirections.actionNotesByCategoryFragmentToEditNoteFragment(
                    it.noteId,
                    it.title,
                    it.description,
                    it.category,
                    it.color,
                    destination
                )
            view.findNavController().navigate(editNoteNavDirection)
        })

        binding.noteByCategoryList.adapter = adapter

        var category = getString(R.string.category)

        categoryList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            it.forEach { note ->
                category = note.category
            }
        })

        val swipe = ItemTouchHelper(swipeNoteToDelete())
        swipe.attachToRecyclerView(binding.noteByCategoryList)

        binding.fabAddNote.setOnClickListener {
            val addNoteNavDirection = NotesByCategoryFragmentDirections.actionNotesByCategoryFragmentToAddNoteFragment(category, destination)
            view.findNavController().navigate(addNoteNavDirection)
        }

        binding.lifecycleOwner = this
    }

    private fun swipeNoteToDelete(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                showDeleteNoteDialog(position)
            }

        }
    }

    private fun showDeleteNoteDialog(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.delete_note_dialog))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                sharedViewModel.onDeleteNote(categoryList.value?.get(position)!!.noteId)
                Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                // close the dialog
                adapter.notifyItemChanged(position)
            }
            .show()
    }
}

