package com.hitanshudhawan.popcorn.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hitanshudhawan.popcorn.R;
import com.hitanshudhawan.popcorn.fragments.FavouritesFragment;
import com.hitanshudhawan.popcorn.fragments.MoviesFragment;
import com.hitanshudhawan.popcorn.fragments.TVShowsFragment;
import com.hitanshudhawan.popcorn.utils.Constants;
import com.hitanshudhawan.popcorn.utils.NetworkConnection;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int itemId;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (sharedPreferences.getBoolean(Constants.FIRST_TIME_LAUNCH, true)) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(Constants.FIRST_TIME_LAUNCH, false);
            sharedPreferencesEditor.apply();
        }

        initUi();
        initListener();
        setFragment(new MoviesFragment());
    }

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.getMenu().findItem(R.id.nav_movies).setChecked(true);
    }

    private void initListener() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                itemId =  item.getItemId();
                return menuItem(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_movies_tv_shows_people));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!NetworkConnection.isConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(Constants.QUERY, query);
                startActivity(intent);
                searchMenuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public boolean menuItem(boolean menuItem) {

        switch (itemId) {
            case R.id.nav_movies:
                setTitle(R.string.movies);
                setFragment(new MoviesFragment());
                return true;
            case R.id.nav_tv_shows:
                setTitle(R.string.tv_shows);
                setFragment(new TVShowsFragment());
                return true;
            case R.id.nav_favorites:
                setTitle(R.string.favorites);
                setFragment(new FavouritesFragment());
                return true;
            case R.id.nav_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return false;
        }
        return menuItem;
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
