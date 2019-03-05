package com.openclassrooms.realestatemanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.realestatemanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
    List<Address> addresses;
    ArrayList<String> ids = new ArrayList<>();
    Geocoder geocoder;
    ArrayList<LatLng> locations = new ArrayList<>();
    List<Address> theAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkPlayServices();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        geocoder = new Geocoder(this, Locale.getDefault());


        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                try {
                    addresses = geocoder.getFromLocationName((String) documentSnapshot.get("location"), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locations.add(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                ids.add((String) documentSnapshot.get("id"));
                Log.d("location", String.valueOf(addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude()));
            }
            AddPinsOnMap();

        });


    }


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        Intent intent = getIntent();
        String focus = intent.getStringExtra("focus");
        if (focus != null) {

            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                theAddress = geocoder.getFromLocationName(focus, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(theAddress.get(0).getLatitude(), theAddress.get(0).getLongitude()), 12f));

        }


        mMap.setOnMarkerClickListener(this);

    }

    private void AddPinsOnMap() {
        for (int i = 0; i < locations.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(locations.get(i))).setTag(ids.get(i));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String markerObject = (String) marker.getTag();

        Intent myIntent = new Intent(this, DetailActivity.class);
        SharedPreferences mPreferences = getSharedPreferences("SHARED", MODE_PRIVATE);
        myIntent.putExtra("id", markerObject);
        mPreferences.edit().putString("id", markerObject).apply();
        startActivity(myIntent);
        return true;
    }
}
