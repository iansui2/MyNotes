package com.iansui.mynotes.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.iansui.mynotes.R
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.FragmentAddNoteBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory

class AddNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private lateinit var sharedViewModel: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val fragmentAddNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        binding = fragmentAddNoteBinding
        return fragmentAddNoteBinding.root
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

        binding.fabSaveNote.setOnClickListener {
            getCategory()
            getTitle()
            getDescription()
            binding.categoryEditText.hideKeyboard()
            binding.titleEditText.hideKeyboard()
            binding.descEditText.hideKeyboard()
            sharedViewModel.onSaveNote()
            Toast.makeText(context, "Note Added!", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(AddNoteFragmentDirections.actionAddNoteFragmentToNotesFragment())
        }

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
}