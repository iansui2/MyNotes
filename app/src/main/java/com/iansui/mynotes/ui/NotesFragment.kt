package com.iansui.mynotes.ui

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iansui.mynotes.MainActivity
import com.iansui.mynotes.R
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.FragmentNotesBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory
import java.util.*

class NotesFragment : Fragment() {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var sharedViewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter
    private lateinit var subMenu: SubMenu
    private var temporaryCategoryList: ArrayList<String> = arrayListOf()
    private var notesStatus: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val fragmentNotesBinding = FragmentNotesBinding.inflate(inflater, container, false)
        binding = fragmentNotesBinding
        return fragmentNotesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application: Application = requireNotNull(this.activity).application

        val dataSource = NotesRepository(NotesDatabase.getInstance(application).notesDAO)

        val viewModelFactory = NotesViewModelFactory(dataSource)

        val mainActivity: MainActivity = activity as MainActivity
        subMenu = mainActivity.subMenu
        temporaryCategoryList = mainActivity.temporaryCategoryList

        sharedViewModel = ViewModelProvider(this, viewModelFactory)
                .get(NotesViewModel::class.java)

        binding.viewModel = sharedViewModel

        val destination = getString(R.string.NotesFragment)

        adapter = NotesAdapter(NotesAdapter.OnClickListener {
            val editNoteNavDirection = NotesFragmentDirections.actionNotesFragmentToEditNoteFragment(it.noteId, it.title, it.description, it.category, it.color, destination)
            view.findNavController().navigate(editNoteNavDirection)
        })

        binding.noteList.adapter = adapter

        sharedViewModel.notes.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    binding.introductionText.isVisible = false
                    notesStatus = true
                } else {
                    notesStatus = false
                }
                adapter.submitList(it)
            }
        })

        val swipe = ItemTouchHelper(swipeNoteToDelete())
        swipe.attachToRecyclerView(binding.noteList)

        binding.fabAddNote.setOnClickListener {
            val addNoteNavDirection = NotesFragmentDirections.actionNotesFragmentToAddNoteFragment("", destination)
            view.findNavController().navigate(addNoteNavDirection)
        }

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this
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
                    sharedViewModel.onDeleteNote(sharedViewModel.notes.value?.get(position)!!.noteId)
                    subMenu.clear()
                    temporaryCategoryList.clear()
                    sharedViewModel.categories.observe(this, { categories ->
                        categories.forEach { category ->
                            if (!temporaryCategoryList.contains(category)) {
                                val subMenuItem = subMenu.add(category)
                                temporaryCategoryList.add(category)
                                subMenuItem.title = category
                                subMenuItem.setIcon(R.drawable.ic_category)
                            }
                        }
                    })
                    Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show()
                    binding.introductionText.isVisible = true
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // close the dialog
                    adapter.notifyItemChanged(position)
                }.show()
    }

    private fun showDeleteAllNoteDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_all))
                .setMessage(getString(R.string.delete_all_dialog))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    sharedViewModel.onDeleteAllNotes()
                    subMenu.clear()
                    temporaryCategoryList.clear()
                    Toast.makeText(context, "Notes Deleted!", Toast.LENGTH_SHORT).show()
                    binding.introductionText.isVisible = true
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // close the dialog
                }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.all_notes_menu, menu)
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val deleteAllMenu = menu.findItem(R.id.delete_all_item)
        deleteAllMenu.isEnabled = notesStatus
    }
}