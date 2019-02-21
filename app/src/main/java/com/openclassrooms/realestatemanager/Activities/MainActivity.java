package com.openclassrooms.realestatemanager.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.openclassrooms.realestatemanager.Fragments.DetailFragment;
import com.openclassrooms.realestatemanager.Fragments.MainFragment;
import com.openclassrooms.realestatemanager.R;

public class MainActivity extends AppCompatActivity implements MainFragment.OnButtonClickedListener {

    //Declare our two fragments
    private MainFragment mainFragment;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configure and show it
        this.configureAndShowMainFragment();
        this.configureAndShowDetailFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp);
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
                //mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case (R.id.navbar_add):
//                Intent addIntent = new Intent(this, AddActivity.class);
//                startActivity(addIntent);
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
}