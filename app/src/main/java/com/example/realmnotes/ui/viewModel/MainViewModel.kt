package com.example.realmnotes.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realmnotes.data.Note
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import java.util.*

class MainViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()

    val allNotes: LiveData<List<Note>>
        get() = getAllNotes()

    fun addNote(noteTitle: String, noteDes: String) {
        realm.executeTransaction { realm ->
            val note = realm.createObject(Note::class.java, UUID.randomUUID().toString())
            note.title = noteTitle
            note.description = noteDes
            realm.insertOrUpdate(note)
        }
    }

    fun updateNote(id: String, noteTitle: String, noteDesc: String) {
        val target = realm.where(Note::class.java).equalTo("id", id).findFirst()

        realm.executeTransaction {
            target?.title = noteTitle
            target?.description = noteDesc
            if (target != null) {
                realm.insertOrUpdate(target)
            }
        }
    }


    fun deleteNote(id: String) {
        realm.executeTransaction {
            realm.where(Note::class.java).equalTo("id", id).findFirst()?.deleteFromRealm()
        }
    }

    fun deleteAllNotes() {
        realm.executeTransaction { realm ->
            realm.deleteAll()
        }
    }

    private fun getAllNotes(): MutableLiveData<List<Note>> {
        val list = MutableLiveData<List<Note>>()
        val notes = realm.where(Note::class.java).findAll()
        list.value = notes?.subList(0, notes.size)
        return list
    }

}