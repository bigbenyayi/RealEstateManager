package com.openclassrooms.realestatemanager.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.realestatemanager.Fragments.DetailFragment;
import com.openclassrooms.realestatemanager.Fragments.MainFragment;
import com.openclassrooms.realestatemanager.Models.DatabaseHouseItem;
import com.openclassrooms.realestatemanager.Models.DialogBuilder;
import com.openclassrooms.realestatemanager.Models.ItemDao;
import com.openclassrooms.realestatemanager.Models.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainFragment.OnButtonClickedListener, NavigationView.OnNavigationItemSelectedListener, DialogBuilder.AlertDialogListener {

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    //Declare our two fragments
    private MainFragment mainFragment;
    private DetailFragment detailFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    ArrayList poi = new ArrayList();
    ArrayList pics = new ArrayList();
    ArrayList rooms = new ArrayList();

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

////////////////////////////////////////////////////////////////////////////////



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
            case R.id.navbar_search:
                SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !mPrefs.getBoolean("searchBoolean", false)) {
                    item.setIcon(getDrawable(R.drawable.ic_clear_black_24dp));
                    mPrefs.edit().putBoolean("searchBoolean", true).apply();
                    openSearchAlertDialog();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mPrefs.getBoolean("searchBoolean", false)) {
                    mPrefs.edit().putBoolean("searchBoolean", false).apply();
                    item.setIcon(getDrawable(R.drawable.ic_clear_black_24dp));
                    openSearchAlertDialog();
                }
                break;
        }

        return true;
    }

    private void openSearchAlertDialog() {
        DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.show(getSupportFragmentManager(), "example dialog");
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
        getMenuInflater().inflate(R.menu.navbar_main_menu, menu);

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
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                SharedPreferences mPrefs;
                startActivity(settingsIntent);

                break;

            case R.id.navbar_logout:
                mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
                mPrefs.edit().putString("username", null).apply();
                mPrefs.edit().putString("email", null).apply();

                this.signOutUserFromFirebase();

                Intent myIntent = new Intent(this, LoginActivity.class);
                startActivity(myIntent);


                break;
            case R.id.navbar_map:

                Intent myMapIntent = new Intent(this, MapsActivity.class);
                startActivity(myMapIntent);

                //Show map with pins on the house lol
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

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                case DELETE_USER_TASK:
                    finish();
                    break;
                default:
                    break;

            }
        };
    }

    @Override
    public void fetchData(String city, int roomsMin, int roomsMax, boolean sold, boolean available, String beginDate, String endDate, int photosMin, int photosMax, boolean park, boolean school, boolean restaurant, int surfaceMin, int surfaceMax, int priceMin, int priceMax) {
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        mPrefs.edit().putString("city", city).apply();
        mPrefs.edit().putInt("roomsMin", roomsMin).apply();
        mPrefs.edit().putInt("roomsMax", roomsMax).apply();
        mPrefs.edit().putBoolean("sold", sold).apply();
        mPrefs.edit().putBoolean("available", available).apply();
        mPrefs.edit().putString("beginDate", beginDate).apply();
        mPrefs.edit().putString("endDate", endDate).apply();
        mPrefs.edit().putInt("photosMin", photosMin).apply();
        mPrefs.edit().putInt("photosMax", photosMax).apply();
        mPrefs.edit().putBoolean("park", park).apply();
        mPrefs.edit().putBoolean("school", school).apply();
        mPrefs.edit().putBoolean("restaurant", restaurant).apply();
        mPrefs.edit().putInt("surfaceMin", surfaceMin).apply();
        mPrefs.edit().putInt("surfaceMax", surfaceMax).apply();
        mPrefs.edit().putInt("priceMax", priceMax).apply();
        mPrefs.edit().putInt("priceMin", priceMin).apply();
    }
}
