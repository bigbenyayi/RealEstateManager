package com.openclassrooms.realestatemanager.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.realestatemanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng location;


    public MapsFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);


        try {
            addresses = geocoder.getFromLocationName(mPrefs.getString("miniMapLocation", null), 1);
            location = (new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
            Log.d("location", String.valueOf(addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude()));

            AddPinsOnMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        checkPlayServices();
        configurePlayServiceMaps();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11f));


    }

    private void AddPinsOnMap() {

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location));
        }
    }

    public void configurePlayServiceMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
        }
        // Build the map
        getChildFragmentManager().beginTransaction().replace(R.id.mapFrag, mMapFragment).commit();
    }
}
