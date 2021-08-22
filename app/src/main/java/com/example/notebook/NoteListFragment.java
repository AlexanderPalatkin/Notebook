package com.example.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoteListFragment extends Fragment {

    private static final String TAG = "NoteSourceFirebaseImpl";
    private static int positionToList;
    private final String ACTION_DEL_NOTE = "ACTION_DEL_NOTE";
    private final ArrayList<NoteEntity> noteList = new ArrayList<>();
    private boolean SET_UPDATE_NOTE = false;
    private Button createButton;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private CollectionReference noteCollection;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement Controller");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        createButton = view.findViewById(R.id.create_note_button);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        setHasOptionsMenu(true);

        FirebaseApp.initializeApp(requireContext());
        FirebaseFirestore myDB = FirebaseFirestore.getInstance();
        noteCollection = myDB.collection("notes");
        if (!SET_UPDATE_NOTE) {
            initListBD(noteList);
            SET_UPDATE_NOTE = false;
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.note_list_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.clear_note_list)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes_clear_list,
                            (dialog, which) -> {
                                for (NoteEntity note : noteList) {
                                    deleteFromBD(note);
                                }
                                noteList.clear();
                                renderList(noteList);
                            })
                    .setNegativeButton(R.string.no_clear_list, null);
            AlertDialog alertClearList = builder.create();
            alertClearList.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        adapter = new NotesAdapter();
        adapter.setOnItemClickListener((position, action) -> {
            if (action == adapter.ACTION_CHANGE) {
                getController().editNote(noteList.get(position));
                adapter.notifyItemChanged(position);
            }
            if (action == adapter.ACTION_DELETE) {
                noteList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), LinearLayout.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.separator, null)));
        recyclerView.addItemDecoration(itemDecoration);
        renderList(noteList);
        createButton.setOnClickListener(v -> {
            getController().createNewNote();
        });
    }

    public void addNote(NoteEntity newNote) {
        NoteEntity sameNote = findNoteWithId(newNote.getId());
        if (sameNote != null) {
            updateNoteFromBD(newNote);
            SET_UPDATE_NOTE = true;
            sameNote.update(newNote);
        } else {
            addNoteToBD(newNote);
            noteList.add(newNote);
        }
        renderList(noteList);
    }

    public void deleteNote(NoteEntity delNote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.warning)
                .setMessage(R.string.delete_note)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_delete_note,
                        (dialog, which) -> {
                            NoteEntity sameNote = findNoteWithId(delNote.getId());
                            if (sameNote != null) {
                                noteList.remove(sameNote);
                            }
                            deleteFromBD(sameNote);
                            renderList(noteList, ACTION_DEL_NOTE);
                        })
                .setNegativeButton(R.string.no_clear_list, null);
        AlertDialog alertClearList = builder.create();
        alertClearList.show();
    }

    private NoteEntity findNoteWithId(String id) {
        for (NoteEntity note : noteList) {
            if (note.getId().equals(id)) {
                return note;
            }
        }
        return null;
    }

    private void renderList(ArrayList<NoteEntity> noteList) {
        adapter.setData(noteList);
        adapter.notifyDataSetChanged();
    }

    private void renderList(List<NoteEntity> notes, String action) {
        adapter.setData(notes);
        switch (action) {
            case ACTION_DEL_NOTE:
                adapter.notifyItemRemoved(positionToList);
        }
    }

    public void initListBD(ArrayList<NoteEntity> noteList) {
        noteList.clear();
        noteCollection.orderBy(NoteMapping.Fields.DATE,
                Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> doc = document.getData();
                            NoteEntity note = NoteMapping.toNote(doc);
                            noteList.add(note);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void addNoteToBD(final NoteEntity note) {
        noteCollection.add(NoteMapping.toDocument(note))
                .addOnSuccessListener(documentReference ->
                        Log.d("BD", "Сделана новая запись в БД: " + note.getTitle()));
        adapter.notifyDataSetChanged();
    }

    private void updateNoteFromBD(NoteEntity sameNote) {
        noteCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> doc = document.getData();
                            if (doc.get("id").equals(sameNote.getId())) {
                                String id = document.getId();
                                noteCollection.document(id).set(NoteMapping.toDocument(sameNote));
                            }
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error update documents.", task.getException());
                    }
                });
    }

    private void deleteFromBD(NoteEntity sameNote) {
        noteCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> doc = document.getData();
                            if (doc.get("id").equals(sameNote.getId())) {
                                String id = document.getId();
                                noteCollection.document(id).delete();
                            }
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error remove documents.", task.getException());
                    }
                });
    }

    private Controller getController() {
        return ((Controller) getActivity());
    }

    interface Controller {
        void createNewNote();

        void deleteNote(NoteEntity delNote);

        void editNote(NoteEntity noteEntity);
    }
}
