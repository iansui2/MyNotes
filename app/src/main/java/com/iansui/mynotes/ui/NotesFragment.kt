package com.iansui.mynotes.ui

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iansui.mynotes.R
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.FragmentNotesBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory

class NotesFragment : Fragment() {

    private lateinit var sharedViewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentNotesBinding.inflate(inflater)

        val application: Application = requireNotNull(this.activity).application

        val dataSource = NotesRepository(NotesDatabase.getInstance(application).notesDAO)

        val viewModelFactory = NotesViewModelFactory(dataSource)

        sharedViewModel = ViewModelProvider(this, viewModelFactory)
                .get(NotesViewModel::class.java)

        binding.viewModel = sharedViewModel

        adapter = NotesAdapter(NotesAdapter.OnClickListener {
            val editNoteNavDirection = NotesFragmentDirections.actionNotesFragmentToEditNoteFragment(it.id, it.title, it.description)
            view?.findNavController()?.navigate(editNoteNavDirection)
        })

        binding.noteList.adapter = adapter

        sharedViewModel.notes.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        val swipe = ItemTouchHelper(swipeNoteToDelete())
        swipe.attachToRecyclerView(binding.noteList)

        binding.fabAddNote.setOnClickListener { view:View ->
            view.findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddNoteFragment())
        }

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this

        return binding.root
    }

    private fun swipeNoteToDelete(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
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
                    sharedViewModel.onDeleteNote(sharedViewModel.notes.value?.get(position)!!.id)
                    Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // close the dialog
                    adapter.notifyItemChanged(position)
                }
                .show()
    }

    private fun showDeleteAllNoteDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_all))
                .setMessage(getString(R.string.delete_all_dialog))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    sharedViewModel.onDeleteAllNotes()
                    Toast.makeText(context, "Notes Deleted!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // close the dialog
                }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_all_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_item -> {
                showDeleteAllNoteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}