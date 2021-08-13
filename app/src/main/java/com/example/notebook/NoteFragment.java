package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class NoteFragment extends Fragment {
    public static final String NOTE_ARGS_KEY = "NOTE_ARGS_KEY";

    @Nullable
    private NoteEntity note = null;
    private Button saveButton;
    private EditText titleEt;
    private EditText descriptionEt;
    private TextView date;

    public static NoteFragment newInstance(NoteEntity noteEntity) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_ARGS_KEY, noteEntity);
        noteFragment.setArguments(args);
        return noteFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implements Controller");
        }
        if (getArguments() != null) {
            note = getArguments().getParcelable(NOTE_ARGS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        saveButton = view.findViewById(R.id.save_changes);
        titleEt = view.findViewById(R.id.title_fragment_note);
        descriptionEt = view.findViewById(R.id.descriptions_fragment_note);
        date = view.findViewById(R.id.date);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        assert getArguments() != null;
        note = (NoteEntity)getArguments().getParcelable(NOTE_ARGS_KEY);
        fillNote(note);
        saveButton.setOnClickListener(v -> {
            getContract().saveNote(gatherNote());
            date.setText(String.valueOf(new Date()));
        });
    }

    private void fillNote(NoteEntity note) {
        if (note == null) return;
        titleEt.setText(note.getTitle());
        descriptionEt.setText(note.getDescription());
        date.setText(String.valueOf(note.getDate()));
    }

    private NoteEntity gatherNote() {
        return new NoteEntity(
                note == null ? NoteEntity.generateNewId() : note.getId(),
                titleEt.getText().toString(),
                descriptionEt.getText().toString()
        );
    }

    private Controller getContract() {
        return (Controller) getActivity();
    }

    interface Controller {
        void saveNote(NoteEntity note);
    }
}
