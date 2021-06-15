package com.iansui.mynotes.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iansui.mynotes.R
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.FragmentEditNoteBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory


class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var sharedViewModel: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val fragmentEditNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        binding = fragmentEditNoteBinding
        return fragmentEditNoteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val dataSource = NotesRepository(NotesDatabase.getInstance(application).notesDAO)
        val viewModelFactory = NotesViewModelFactory(dataSource)
        sharedViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NotesViewModel::class.java)

        binding.viewModel = sharedViewModel

        binding.categoryEditText.openKeyboard()
        binding.titleEditText.openKeyboard()
        binding.descEditText.openKeyboard()

        sharedViewModel.categories.observe(viewLifecycleOwner, { categories ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, categories)
            binding.categoryEditText.threshold = 1
            binding.categoryEditText.setAdapter(adapter)
        })

        val args = EditNoteFragmentArgs.fromBundle(requireArguments())
        sharedViewModel.setTitle(args.title)
        sharedViewModel.setDescription(args.description)
        sharedViewModel.setCategory(args.category)

        binding.fabSaveNote.setOnClickListener {
            getCategory()
            getTitle()
            getDescription()
            binding.categoryEditText.hideKeyboard()
            binding.titleEditText.hideKeyboard()
            binding.descEditText.hideKeyboard()
            sharedViewModel.onUpdateNote(args.id)
            Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(EditNoteFragmentDirections.actionEditNoteFragmentToNotesFragment())
        }

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this
    }

    private fun View.openKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInputFromWindow(windowToken, InputMethodManager.SHOW_FORCED, 0)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun getTitle() {
        val title = if (binding.titleEditText.text.toString() != "") {
            binding.titleEditText.text.toString()
        } else {
            getString(R.string.title)
        }

        sharedViewModel.setTitle(title)
    }

    private fun getDescription () {
        val description = if (binding.descEditText.text.toString() != "") {
            binding.descEditText.text.toString()
        } else {
            getString(R.string.description)
        }

        sharedViewModel.setDescription(description)
    }

    private fun getCategory () {
        val category = if (binding.categoryEditText.text.toString() != "") {
            binding.categoryEditText.text.toString()
        } else {
            ""
        }

        sharedViewModel.setCategory(category)
    }

    private fun showDeleteNoteDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_note))
                .setMessage(getString(R.string.delete_note_dialog))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    findNavController().navigate(R.id.action_editNoteFragment_to_notesFragment)
                    val args = EditNoteFragmentArgs.fromBundle(requireArguments())
                    sharedViewModel.onDeleteNote(args.id)
                    Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // close the dialog
                }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_item -> {
                binding.titleEditText.hideKeyboard()
                binding.descEditText.hideKeyboard()
                showDeleteNoteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}