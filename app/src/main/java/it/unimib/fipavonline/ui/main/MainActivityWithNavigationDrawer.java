package it.unimib.fipavonline.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import it.unimib.fipavonline.R;

/**
 * Activity that contains Fragments, managed by a NavigationView (NavigationDrawer),
 * that show the campionato.
 */
public class MainActivityWithNavigationDrawer extends AppCompatActivity {

    private static final String TAG = MainActivityWithNavigationDrawer.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_navigation_drawer);

        Toolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_campionato_list, R.id.fragment_partita_list,
                R.id.fragment_favorite_campionato, R.id.fragment_settings).setOpenableLayout(drawerLayout)
                .build();

        navView.setCheckedItem(R.id.fragment_campionato_list);

        // For the Toolbar
        NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);

        // For the NavigationView (NavigationDrawer)
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        NavDestination navDestination = navController.getCurrentDestination();
        if (navDestination != null) {
            int currentDestination = navController.getCurrentDestination().getId();
            if(currentDestination == R.id.fragment_campionato_list){
                navView.setCheckedItem(R.id.fragment_campionato_list);
            } else if(currentDestination == R.id.fragment_partita_list){
                navView.setCheckedItem(R.id.fragment_partita_list);
            } else if(currentDestination == R.id.fragment_favorite_campionato){
                navView.setCheckedItem(R.id.fragment_favorite_campionato);
            } else if (currentDestination == R.id.fragment_settings) {
                navView.setCheckedItem(R.id.fragment_settings);
            }
        }
    }
}
