package com.example.realmnotes.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realmnotes.R
import com.example.realmnotes.data.Note
import com.example.realmnotes.databinding.ActivityMainBinding
import com.example.realmnotes.view.adapter.NotesAdapter
import com.example.realmnotes.view.viewModel.MainViewModel

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
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        viewModel.allNotes.observe(this) { allNotes ->
            notesAdapter.submitList(allNotes)
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
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.update_dialog, viewGroup, false)
        val builder = AlertDialog.Builder(this)

        val titleEdt: EditText = dialogView.findViewById(R.id.titleEditText_update)
        val descriptionEdt: EditText = dialogView.findViewById(R.id.descriptionEditText_update)

        titleEdt.setText(note.title)
        descriptionEdt.setText(note.description)

        builder.setView(dialogView)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.delete_all, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAll) {
            viewModel.deleteAllNotes()
            notesAdapter.notifyDataSetChanged()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}