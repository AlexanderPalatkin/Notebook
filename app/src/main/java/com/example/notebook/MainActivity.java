package com.example.notebook;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new NoteListFragment())
                .commit();

        initView();
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (navigateFragment(id)) {
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
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
        }
        return false;
    }
}