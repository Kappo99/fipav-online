package it.unimib.fipavonline.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.databinding.ActivityMainWithBottomNavigationViewBinding;

/**
 * Activity that contains Fragments, managed by a BottomNavigationView, that
 * show the campionato.
 */
public class MainActivityWithBottomNavigationView extends AppCompatActivity {

    private static final String TAG = MainActivityWithBottomNavigationView.class.getSimpleName();

    private ActivityMainWithBottomNavigationViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainWithBottomNavigationViewBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.topAppbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_campionato_list, R.id.fragment_partita_list,
                R.id.fragment_favorite_campionato, R.id.fragment_settings).build();

        // For the Toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // For the BottomNavigationView
        NavigationUI.setupWithNavController(bottomNav, navController);

        Intent intent = getIntent();
    }
}
