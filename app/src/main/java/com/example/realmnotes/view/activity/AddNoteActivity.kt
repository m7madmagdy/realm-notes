package com.example.realmnotes.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.realmnotes.databinding.ActivityAddNoteBinding
import com.example.realmnotes.view.viewModel.MainViewModel

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        saveNote()
    }

    private fun saveNote() {
        binding.saveButton.setOnClickListener {
            if (isValid()) {
                return@setOnClickListener
            } else {
                addNote()
                startActivity(Intent(this, NotesActivity::class.java))
                finish()
            }
        }
    }

    private fun addNote() {
        viewModel.addNote(
            binding.titleEditText.text.toString(),
            binding.descriptionEditText.text.toString()
        )
    }

    private fun isValid(): Boolean {
        return binding.titleEditText.text.toString()
            .isEmpty() || binding.descriptionEditText.text.toString().isEmpty()
    }
}