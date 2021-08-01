package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NoteListFragment extends Fragment {

    private LinearLayout linearLayout;

    public interface Controller {
        void openNoteScreen(NoteEntity noteEntity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout = view.findViewById(R.id.list_container);
        addNotesToList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteScreen");
        }
    }

    private void addNotesToList() {
        String[] notesArray = getResources().getStringArray(R.array.notes);
        for (int i = 0; i < notesArray.length; i++) {
            String note = notesArray[i];
            TextView textView = new TextView(getContext());
            textView.setText(note);
            textView.setTextSize(30);
            final int finalIndex = i;
            textView.setOnClickListener(v -> {
                ((Controller) requireActivity()).openNoteScreen(new NoteEntity(note,
                        getResources().getStringArray(R.array.descriptions)[finalIndex]));
            });
            linearLayout.addView(textView);
        }
    }
}
