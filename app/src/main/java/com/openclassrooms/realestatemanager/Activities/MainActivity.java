package com.openclassrooms.realestatemanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.realestatemanager.Fragments.DetailFragment;
import com.openclassrooms.realestatemanager.Fragments.MainFragment;
import com.openclassrooms.realestatemanager.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements MainFragment.OnButtonClickedListener, NavigationView.OnNavigationItemSelectedListener {

    //Declare our two fragments
    private MainFragment mainFragment;
    private DetailFragment detailFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.navigationDrawer);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);


        mNavigationView.setNavigationItemSelectedListener(this);




        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#FFFFFF"));
        toggle.syncState();


        //Configure and show it
        this.configureAndShowMainFragment();
        this.configureAndShowDetailFragment();
        this.configureNavigationView();
    }

    // --------------
    // CallBack
    // --------------

    @Override
    public void onButtonClicked(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (android.R.id.home):
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case (R.id.navbar_add):
                Intent addIntent = new Intent(this, AddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.navbar_edit:
                //Edit Activity
                break;
            case R.id.navbar_search:
                //AlertDialog Search Thing
                break;
        }

        return true;
    }

    private void configureNavigationView() {

        // Configure NavigationHeader
        configureNavigationHeader();
        // Mark as selected the first menu item
        this.mNavigationView.getMenu().getItem(0).setChecked(true);
        // Subscribes to listen the navigationView
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    // Configure NavigationHeader
    private void configureNavigationHeader() {

        TextView userName = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_name);
        TextView userEmail = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_email);
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);

        //Update views with data
        userName.setText(mPrefs.getString("username", "Username"));
        userEmail.setText(mPrefs.getString("email", "email"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar_menu, menu);

        return true;
    }


    // --------------
    // FRAGMENTS
    // --------------

    public void configureAndShowMainFragment() {

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_main);

        if (mainFragment == null) {
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, mainFragment)
                    .commit();
        }
    }

    public void configureAndShowDetailFragment() {
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null && findViewById(R.id.frame_layout_detail) != null) {
            detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.navbar_settings:
                Toast.makeText(this, "Settings activity", Toast.LENGTH_SHORT).show();

                break;

            case R.id.navbar_logout:

                SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
                mPrefs.edit().putString("username", null).apply();
                mPrefs.edit().putString("email", null).apply();

                Intent myIntent = new Intent(this, LoginActivity.class);
                startActivity(myIntent);
                break;

            default:
                break;
        }
        // Close menu drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        // Close the menu so open and if the touch return is pushed
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
