package com.example.notebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {
    private static final String NOTES_LIST_FRAGMENT_TAG = "NOTES_LIST_FRAGMENT_TAG";
    private boolean isTwoPaneMod = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isTwoPaneMod = findViewById(R.id.detail_container) != null;
        showNoteList();
        initView();
    }

    private void showNoteList() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new NoteListFragment(), NOTES_LIST_FRAGMENT_TAG)
                .commit();
    }

    private void showEditNote() {
        showEditNote(null);
    }

    private void showEditNote(@Nullable NoteEntity note) {
        if (!isTwoPaneMod) {
            setTitle(note == null ? R.string.create_note_title : R.string.edit_note_title);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!isTwoPaneMod) {
            transaction.addToBackStack(null);
        }
        transaction.replace(isTwoPaneMod ? R.id.detail_container : R.id.main_container,
                NoteFragment.newInstance(note))
                .commit();
    }

    @Override
    public void saveNote(NoteEntity note) {
        setTitle(R.string.app_name);
        getSupportFragmentManager().popBackStack();
        NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager()
                .findFragmentByTag(NOTES_LIST_FRAGMENT_TAG);
        assert noteListFragment != null;
        noteListFragment.addNote(note);
    }

    @Override
    public void createNewNote() {
        showEditNote();
    }

    @Override
    public void editNote(NoteEntity note) {
        showEditNote(note);
    }

    private void initView() {
        androidx.appcompat.widget.Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    // регистрация drawer
    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();
        if (navigateFragment(id)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean navigateFragment(int id) {
        switch (id) {
            case R.id.about:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new AboutFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.back:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new NoteListFragment())
                        .commit();
                return true;
        }
        return false;
    }
}