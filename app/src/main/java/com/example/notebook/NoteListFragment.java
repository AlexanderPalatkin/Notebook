package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    private Button createButton;
    private RecyclerView recyclerView;
    private final ArrayList<NoteEntity> noteList = new ArrayList<>();
    private NotesAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        createButton = view.findViewById(R.id.create_note_button);
        recyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        adapter = new NotesAdapter();
        adapter.setOnItemClickListener(getController()::editNote);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        renderList(noteList);
        createButton.setOnClickListener(v -> {
            getController().createNewNote();
        });
    }

    public void addNote(NoteEntity newNote) {
        NoteEntity sameNote = findNoteWithId(newNote.getId());
        if (sameNote != null) {
            noteList.remove(sameNote);
        }
        noteList.add(newNote);
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

    private Controller getController() {
        return ((Controller) getActivity());
    }

    interface Controller {
        void createNewNote();
        void editNote(NoteEntity noteEntity);
    }
}
