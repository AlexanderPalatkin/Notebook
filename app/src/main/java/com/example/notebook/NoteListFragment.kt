package com.example.notebook

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.google.firebase.FirebaseApp
import android.content.DialogInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import android.widget.LinearLayout
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.lang.RuntimeException
import java.util.*

class NoteListFragment : Fragment() {
    private val ACTION_DEL_NOTE = "ACTION_DEL_NOTE"
    private val noteList = ArrayList<NoteEntity>()
    private var SET_UPDATE_NOTE = false
    private var createButton: Button? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: NotesAdapter? = null
    private var noteCollection: CollectionReference? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is Controller) {
            throw RuntimeException("Activity must implement Controller")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)
        createButton = view.findViewById(R.id.create_note_button)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        setHasOptionsMenu(true)
        FirebaseApp.initializeApp(requireContext())
        val myDB = FirebaseFirestore.getInstance()
        noteCollection = myDB.collection("notes")
        if (!SET_UPDATE_NOTE) {
            initListBD(noteList)
            SET_UPDATE_NOTE = false
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.note_list_clear) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.warning)
                .setMessage(R.string.clear_note_list)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.yes_clear_list
                ) { dialog: DialogInterface?, which: Int ->
                    for (note in noteList) {
                        deleteFromBD(note)
                    }
                    noteList.clear()
                    renderList(noteList)
                }
                .setNegativeButton(R.string.no_clear_list, null)
            val alertClearList = builder.create()
            alertClearList.show()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NotesAdapter()
        adapter!!.setOnItemClickListener { position: Int, action: Int ->
            if (action == adapter!!.ACTION_CHANGE) {
                controller!!.editNote(noteList[position])
                adapter!!.notifyItemChanged(position)
            }
            if (action == adapter!!.ACTION_DELETE) {
                noteList.removeAt(position)
                adapter!!.notifyItemRemoved(position)
            }
        }
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
        val itemDecoration = DividerItemDecoration(requireActivity(), LinearLayout.VERTICAL)
        itemDecoration.setDrawable(
            Objects.requireNonNull(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.separator, null
                )
            )!!
        )
        recyclerView!!.addItemDecoration(itemDecoration)
        renderList(noteList)
        createButton!!.setOnClickListener { v: View? -> controller!!.createNewNote() }
    }

    fun addNote(newNote: NoteEntity) {
        val sameNote = findNoteWithId(newNote.id.toString())
        if (sameNote != null) {
            updateNoteFromBD(newNote)
            SET_UPDATE_NOTE = true
            sameNote.update(newNote)
        } else {
            addNoteToBD(newNote)
            noteList.add(newNote)
        }
        renderList(noteList)
    }

    fun deleteNote(delNote: NoteEntity) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.warning)
            .setMessage(R.string.delete_note)
            .setCancelable(false)
            .setPositiveButton(
                R.string.yes_delete_note
            ) { dialog: DialogInterface?, which: Int ->
                val sameNote = findNoteWithId(delNote.id.toString())
                if (sameNote != null) {
                    noteList.remove(sameNote)
                }
                deleteFromBD(sameNote)
                renderList(noteList, ACTION_DEL_NOTE)
            }
            .setNegativeButton(R.string.no_clear_list, null)
        val alertClearList = builder.create()
        alertClearList.show()
    }

    private fun findNoteWithId(id: String): NoteEntity? {
        for (note in noteList) {
            if (note.id == id) {
                return note
            }
        }
        return null
    }

    private fun renderList(noteList: ArrayList<NoteEntity>) {
        adapter!!.setData(noteList)
        adapter!!.notifyDataSetChanged()
    }

    private fun renderList(notes: List<NoteEntity>, action: String) {
        adapter!!.setData(notes)
        when (action) {
            ACTION_DEL_NOTE -> adapter!!.notifyItemRemoved(positionToList)
        }
    }

    fun initListBD(noteList: ArrayList<NoteEntity>) {
        noteList.clear()
        noteCollection!!.orderBy(
            NoteMapping.Fields.DATE,
            Query.Direction.DESCENDING
        )
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val doc = document.data
                        val note = NoteMapping.toNote(doc)
                        noteList.add(note)
                        Log.d(TAG, document.id + " => " + document.data)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun addNoteToBD(note: NoteEntity) {
        noteCollection!!.add(NoteMapping.toDocument(note))
            .addOnSuccessListener { documentReference: DocumentReference? ->
                Log.d(
                    "BD",
                    "Сделана новая запись в БД: " + note.title
                )
            }
        adapter!!.notifyDataSetChanged()
    }

    private fun updateNoteFromBD(sameNote: NoteEntity) {
        noteCollection
            ?.get()
            ?.addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val doc = document.data
                        if (doc["id"] == sameNote.id) {
                            val id = document.id
                            noteCollection!!.document(id).set(NoteMapping.toDocument(sameNote))
                        }
                        Log.d(TAG, document.id + " => " + document.data)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    Log.w(TAG, "Error update documents.", task.exception)
                }
            }
    }

    private fun deleteFromBD(sameNote: NoteEntity?) {
        noteCollection
            ?.get()
            ?.addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val doc = document.data
                        if (doc["id"] == sameNote!!.id) {
                            val id = document.id
                            noteCollection!!.document(id).delete()
                        }
                        Log.d(TAG, document.id + " => " + document.data)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    Log.w(TAG, "Error remove documents.", task.exception)
                }
            }
    }

    private val controller: Controller?
        private get() = activity as Controller?

    internal interface Controller {
        fun createNewNote()
        fun deleteNote(delNote: NoteEntity?)
        fun editNote(noteEntity: NoteEntity?)
    }

    companion object {
        private const val TAG = "NoteSourceFirebaseImpl"
        private const val positionToList = 0
    }
}