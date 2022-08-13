package com.example.realmnotes.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.realmnotes.data.Note
import com.example.realmnotes.databinding.NoteCardBinding

class NotesAdapter(
    private val onItemClicked: (v: View, note: Note) -> Unit,
    private val onSwipe: OnSwiper
) :
    ListAdapter<Note, NotesAdapter.MyViewHolder>(MyDiffUtil) {

    object MyDiffUtil : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NoteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
        onSwipe.onClick(note)
    }

    class OnSwiper(val clickListener: (note: Note) -> Unit) {
        fun onClick(note: Note) = clickListener(note)
    }

    inner class MyViewHolder(
        private val binding: NoteCardBinding,
        val onItemCLicked: (view: View, note: Note) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(note: Note?) {
            binding.apply {
                editNote.setOnClickListener(this@MyViewHolder)
                titleTextView.text = note?.title
                descriptionTextView.text = note?.description
            }
        }

        override fun onClick(p0: View?) {
            onItemCLicked(p0!!, getItem(adapterPosition))
        }
    }
}

