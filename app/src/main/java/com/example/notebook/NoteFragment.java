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
import androidx.fragment.app.Fragment;

public class NoteFragment extends Fragment {
    public static final String NOTE_ARGS_KEY = "NOTE_ARGS_KEY";

    private NoteEntity note = null;

    private EditText nameEt;
    private EditText descriptionEt;
    private TextView date;

    public static NoteFragment newInstance(NoteEntity noteEntity) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_ARGS_KEY, noteEntity);
        noteFragment.setArguments(args);
        return noteFragment;
    }

    public interface Controller {
        void saveResult(NoteEntity note);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null);
        nameEt = view.findViewById(R.id.name_fragment_note);
        descriptionEt = view.findViewById(R.id.descriptions_fragment_note);
        date = view.findViewById(R.id.date);
        Button saveButton = view.findViewById(R.id.save_changes);

        saveButton.setOnClickListener(v -> {
            Controller controller = (Controller) getActivity();
            assert controller != null;
            controller.saveResult(new NoteEntity(
                    nameEt.getText().toString(),
                    descriptionEt.getText().toString()
            ));
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nameEt.setText(note.getName());
        descriptionEt.setText(note.getDescription());
        date.setText(note.getDate().toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implements Controller");
        }
        if (getArguments() != null){
            note = getArguments().getParcelable(NOTE_ARGS_KEY);
        }
    }
}
