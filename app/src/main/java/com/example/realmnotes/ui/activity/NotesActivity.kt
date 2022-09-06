package com.example.realmnotes.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realmnotes.R
import com.example.realmnotes.data.Note
import com.example.realmnotes.databinding.ActivityMainBinding
import com.example.realmnotes.databinding.UpdateDialogBinding
import com.example.realmnotes.ui.adapter.NotesAdapter
import com.example.realmnotes.ui.viewModel.MainViewModel
import java.util.concurrent.TimeUnit

class NotesActivity : AppCompatActivity() {
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var viewModel: MainViewModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initRecyclerView()
        initUserClicks()
    }

    private fun initRecyclerView() {
        notesAdapter = NotesAdapter({ view, note ->
            onListItemClick(view, note)
        }, NotesAdapter.OnSwiper {
            id = it.id
        })

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteNote(id!!)
                Toast.makeText(this@NotesActivity, "Note Deleted Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerview)

        binding.notesRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@NotesActivity)
            adapter = notesAdapter
        }
    }

    private fun initUserClicks() {
        binding.addNoteButton.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModel.allNotes.observe(this) {
            notesAdapter.submitList(it)
            binding.notesRecyclerview.adapter = notesAdapter
        }
    }

    private fun onListItemClick(view: View, note: Note) {
        when (view.id) {
            R.id.edit_note -> {
                createUpdateDialog(note)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createUpdateDialog(note: Note) {
        val dialogView = UpdateDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this)

        val titleEdt = dialogView.titleEdtUpdate
        val descriptionEdt = dialogView.descriptionEdtUpdate

        titleEdt.setText(note.title)
        descriptionEdt.setText(note.description)

        builder.setView(dialogView.root)
        builder.setTitle("Update Note")
        builder.setPositiveButton("Update") { _, _ ->
            viewModel.updateNote(
                note.id,
                titleEdt.text.toString(),
                descriptionEdt.text.toString()
            )
            notesAdapter.notifyDataSetChanged()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Toast.makeText(this, "Canceled Update", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {
                viewModel.deleteAllNotes()
                notesAdapter.notifyDataSetChanged()
            }
            R.id.action_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}