package com.example.notebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, new NoteListFragment())
                .commit();
    }

    @Override
    public void saveResult(NoteEntity note) {
        //todo
    }

    @Override
    public void openNoteScreen(NoteEntity noteEntity) {
        boolean isLandscape = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;

        getSupportFragmentManager()
                .beginTransaction()
                .add(isLandscape ? R.id.detail_container : R.id.main_container,
                        NoteFragment.newInstance(noteEntity))
                .addToBackStack(null)
                .commit();
    }
}