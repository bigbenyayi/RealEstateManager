package com.openclassrooms.realestatemanager.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.Activities.AddActivity;
import com.openclassrooms.realestatemanager.Activities.EditActivity;
import com.openclassrooms.realestatemanager.Activities.MapsActivity;
import com.openclassrooms.realestatemanager.Models.DetailHouse;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.MyHorizontalPictureAdapter;
import com.openclassrooms.realestatemanager.Models.SimpleRVAdapter;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class DetailFragment extends BaseFragment {

    TextView description;
    TextView surface;
    TextView rooms;
    TextView bedrooms;
    TextView bathrooms;
    TextView address;
    TextView realtor;
    TextView sold;
    TextView market;
    TextView pointsOfInterest;
    Button addAPicButton;
    ImageView chosenPicIV;
    EditText pictureDescET;
    Button addButton;
    EditText realtorET;
    EditText descriptionET;
    EditText surfaceET;
    EditText addressET;
    EditText interestsET;
    EditText bathroomsET;
    EditText bedroomsET;
    EditText roomsET;
    RecyclerView mRecyclerViewPOI;
    String addUrl;
    Boolean editing = false;
    int numberMaxPOI = 0;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<String> pointsOfInterestValue = new ArrayList<>();

    TextView inter;
    TextView desc;
    TextView surf;
    TextView roo;
    TextView bed;
    TextView bath;
    TextView add;
    TextView media;
    DetailHouse houseItem = null;
    Button seeOnMapButton;
    public static final int PICK_IMAGE_REQUEST = 1;
    Uri mainImageUri;

    private MapsFragment mapsFragment;

    HorizontalRecyclerViewItem photoItem = null;
    private ArrayList<HorizontalRecyclerViewItem> listItems;
    private ArrayList<String> photoItems = new ArrayList<>();
    private ArrayList<String> roomItems = new ArrayList<>();
    private ArrayList<String> interestsArray = new ArrayList<>();
    private MyHorizontalAdapter adapter;
    private MyHorizontalPictureAdapter myOtherAdapter;
    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
    boolean isTablet;


    RecyclerView recyclerView;
    RecyclerView editRecyclerView;


    // --------------
    // BASE METHODS
    // --------------+

    @Override

    protected BaseFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    protected void configureDesign() {
    }

    @Override
    protected void updateDesign() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_detail, container, false);

        recyclerView = result.findViewById(R.id.horizontalRecyclerView);
        editRecyclerView = result.findViewById(R.id.horizontalPictureRecyclerView);
        isTablet = getResources().getBoolean(R.bool.isTablet);


        setHasOptionsMenu(true);


        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();

        configureHorizontalRecyclerView();

        description = result.findViewById(R.id.descriptionTV);
        surface = result.findViewById(R.id.surfaceTV);
        rooms = result.findViewById(R.id.nbrOfRoomsTV);
        bedrooms = result.findViewById(R.id.nbrOfBedroomsTV);
        bathrooms = result.findViewById(R.id.nbrOfBathroomsTV);
        address = result.findViewById(R.id.addressTV);
        pointsOfInterest = result.findViewById(R.id.pointsOfInterestTV);
        realtor = result.findViewById(R.id.realEstateInCharge);
        market = result.findViewById(R.id.onTheMarketSinceTV);
        sold = result.findViewById(R.id.soldTV);
        seeOnMapButton = result.findViewById(R.id.seeOnMapButton);
        addAPicButton = result.findViewById(R.id.addAPictureButton);
        addAPicButton.setVisibility(View.INVISIBLE);
        addAPicButton.setText("Add a picture");
        pictureDescET = result.findViewById(R.id.pictureDescriptionET);
        pictureDescET.setVisibility(View.INVISIBLE);
        chosenPicIV = result.findViewById(R.id.pictureChosenIV);
        chosenPicIV.setVisibility(View.INVISIBLE);
        addButton = result.findViewById(R.id.addButton);
        addButton.setVisibility(View.INVISIBLE);
        mRecyclerViewPOI = result.findViewById(R.id.recyclerViewPOI);

        inter = result.findViewById(R.id.pointsOfInterest);
        desc = result.findViewById(R.id.description);
        surf = result.findViewById(R.id.surface);
        roo = result.findViewById(R.id.nbrOfRooms);
        bed = result.findViewById(R.id.nbrOfBedrooms);
        bath = result.findViewById(R.id.nbrOfBathrooms);
        add = result.findViewById(R.id.address);
        media = result.findViewById(R.id.media);

//        realtorET = result.findViewById(R.id.realEstateInChargeET);
//        descriptionET = result.findViewById(R.id.descriptionET);
        surfaceET = result.findViewById(R.id.surfaceET);
        addressET = result.findViewById(R.id.addressET);
        interestsET = result.findViewById(R.id.pointsOfInterestET);
        bathroomsET = result.findViewById(R.id.nbrOfBathroomsET);
        bedroomsET = result.findViewById(R.id.nbrOfBedroomsET);
        roomsET = result.findViewById(R.id.nbrOfRoomsET);
//        realtorET.setVisibility(View.INVISIBLE);
//        descriptionET.setVisibility(View.INVISIBLE);
        surfaceET.setVisibility(View.INVISIBLE);
        addressET.setVisibility(View.INVISIBLE);
        interestsET.setVisibility(View.INVISIBLE);
        bathroomsET.setVisibility(View.INVISIBLE);
        bedroomsET.setVisibility(View.INVISIBLE);
        roomsET.setVisibility(View.INVISIBLE);

        surface.setVisibility(View.INVISIBLE);
        rooms.setVisibility(View.INVISIBLE);
        bedrooms.setVisibility(View.INVISIBLE);
        bathrooms.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        desc.setVisibility(View.INVISIBLE);
        surf.setVisibility(View.INVISIBLE);
        roo.setVisibility(View.INVISIBLE);
        bed.setVisibility(View.INVISIBLE);
        bath.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        sold.setVisibility(View.INVISIBLE);
        realtor.setVisibility(View.INVISIBLE);
        market.setVisibility(View.INVISIBLE);
        pointsOfInterest.setVisibility(View.VISIBLE);
        inter.setVisibility(View.INVISIBLE);
        seeOnMapButton.setVisibility(View.INVISIBLE);

        media.setText("Please select a house to see the details");

        if (b != null) {
            updateTextView();
        }
        return result;
    }


    public void updateTextView() {

        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();
        String id;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
            id = mPrefs.getString("id", "0");
        }


        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                if (id.equals(documentSnapshot.get("id"))) {

                    String descriptionValue = (String) documentSnapshot.get("description");
                    String surfaceValue = (String) documentSnapshot.get("surface");
                    String nbrOfRooms = (String) documentSnapshot.get("numberOfRooms");
                    String nbrOfBedrooms = (String) documentSnapshot.get("numberOfBedrooms");
                    String nbrOfBathrooms = (String) documentSnapshot.get("numberOfBathrooms");
                    String location = (String) documentSnapshot.get("location");
                    String realtorValue = (String) documentSnapshot.get("realtor");
                    String onMarket = (String) documentSnapshot.get("onMarket");
                    String soldValue = (String) documentSnapshot.get("sold");
                    pointsOfInterestValue = (ArrayList<String>) documentSnapshot.get("pointOfInterest");
                    ArrayList<String> pictures = (ArrayList<String>) documentSnapshot.get("pictures");

                    mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(getContext(), pointsOfInterestValue));
                    Log.d("interestsValueFirst", pointsOfInterestValue.toString());

                    houseItem = new DetailHouse(descriptionValue, surfaceValue, nbrOfRooms,
                            nbrOfBedrooms, nbrOfBathrooms, location,
                            realtorValue, onMarket, soldValue, pointsOfInterestValue, pictures);

                    assert houseItem != null;
                    description.setText(houseItem.getDescription());

                    surface.setText(houseItem.getSurface() + "m²");
                    rooms.setText(houseItem.getNbrOfRooms());
                    bedrooms.setText(houseItem.getNbrOfBedrooms());
                    bathrooms.setText(houseItem.getNbrOfBathrooms());
                    address.setText(houseItem.getAddress());
                    realtor.setText("Real Estate Agent: " + houseItem.getRealtor());
                    market.setText("On market since: " + houseItem.getOnMarket());
                    if (soldValue != null) {
                        sold.setText("Sold: " + houseItem.getSaleDate());
                    } else {
                        sold.setText("Still available");
                    }

                    if (houseItem.getPointsOfInterest() != null) {
                        Log.d("house", String.valueOf(houseItem.getPointsOfInterest()));
                        if (houseItem.getPointsOfInterest().size() > 0) {
                            Log.d("house", houseItem.getPointsOfInterest().get(0));
                        }
                        pointsOfInterest.setText("");
                        for (int i = 0; i < houseItem.getPointsOfInterest().size(); i++) {

                            if (i == houseItem.getPointsOfInterest().size() - 1) {
                                pointsOfInterest.append(houseItem.getPointsOfInterest().get(i));
                            } else {
                                pointsOfInterest.append(houseItem.getPointsOfInterest().get(i) + ", ");

                            }
                        }
                    } else {
                        pointsOfInterest.setVisibility(View.INVISIBLE);
                        inter.setVisibility(View.INVISIBLE);
                        Log.d("house", houseItem.getPointsOfInterest().get(0) + "ELSEEEEEE");

                    }


                }
            }
            seeOnMapButton.setOnClickListener(v -> {
                if (!editing) {
                    Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                    mapsIntent.putExtra("focus", houseItem.getAddress());
                    startActivity(mapsIntent);
                }
            });
        });

        desc.setText("Description");
        surf.setText("Surface");
        roo.setText("Rooms");
        bed.setText("Bedrooms");
        bath.setText("Bathrooms");
        add.setText("Address");
        media.setText("Media");

        surface.setVisibility(View.VISIBLE);
        rooms.setVisibility(View.VISIBLE);
        bedrooms.setVisibility(View.VISIBLE);
        bathrooms.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        surf.setVisibility(View.VISIBLE);
        roo.setVisibility(View.VISIBLE);
        editRecyclerView.setVisibility(View.INVISIBLE);
        bed.setVisibility(View.VISIBLE);
        bath.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        media.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        sold.setVisibility(View.VISIBLE);
        realtor.setVisibility(View.VISIBLE);
        market.setVisibility(View.VISIBLE);
        pointsOfInterest.setVisibility(View.VISIBLE);
        inter.setVisibility(View.VISIBLE);
        if (isTablet) {
            configureAndDisplayMiniMap();
        } else {
            seeOnMapButton.setVisibility(View.VISIBLE);
        }


    }

    public void configureHorizontalRecyclerView() {

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalPictureLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);
        editRecyclerView.setLayoutManager(horizontalPictureLayoutManager);


        listItems = new ArrayList<>();
        listItems.clear();

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Intent iin = getActivity().getIntent();
                Bundle b = iin.getExtras();
                String id;

                if (b != null) {
                    id = (String) b.get("id");
                } else {
                    SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
                    id = mPrefs.getString("id", null);
                }

                if (id.equals(documentSnapshot.get("id"))) {

                    ArrayList<String> pictures = (ArrayList<String>) documentSnapshot.get("pictures");
                    ArrayList<String> rooms = (ArrayList<String>) documentSnapshot.get("rooms");

                    for (int j = 0; j < pictures.size(); j++) {

                        photoItem = new HorizontalRecyclerViewItem(pictures.get(j), rooms.get(j));

                        listItems.add(photoItem);
                        photoItems.add(pictures.get(j));
                        roomItems.add(rooms.get(j));
                    }
                }
            }
        });

        adapter = new MyHorizontalAdapter(getContext(), listItems);
        myOtherAdapter = new MyHorizontalPictureAdapter(getContext(), listItems);
        editRecyclerView.setAdapter(myOtherAdapter);
        recyclerView.setAdapter(adapter);
    }

    public void configureAndDisplayMiniMap() {

        SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);

        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();
        String id;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            id = mPrefs.getString("id", "0");
        }


        if (houseItem != null) {
            mPrefs.edit().putString("miniMapLocation", houseItem.getAddress()).apply();

            mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.miniMapFrameLayout);
            if (mapsFragment == null) {
                mapsFragment = new MapsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.miniMapFrameLayout, mapsFragment)
                        .commit();
            }
        } else {
            notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (id.equals(documentSnapshot.get("id"))) {
                        mPrefs.edit().putString("miniMapLocation", (String) documentSnapshot.get("location")).apply();

                        mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.miniMapFrameLayout);
                        if (mapsFragment == null) {
                            mapsFragment = new MapsFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.miniMapFrameLayout, mapsFragment)
                                    .commit();
                        }
                    }
                }
            });
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (android.R.id.home):
                getActivity().onBackPressed();
                break;
            case R.id.navbar_edit:
                Intent addIntent = new Intent(getContext(), EditActivity.class);
                startActivity(addIntent);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.navbar_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
