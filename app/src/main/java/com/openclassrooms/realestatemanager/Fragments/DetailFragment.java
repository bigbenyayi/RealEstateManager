package com.openclassrooms.realestatemanager.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.openclassrooms.realestatemanager.Activities.MapsActivity;
import com.openclassrooms.realestatemanager.Models.DetailHouse;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.MyHorizontalPictureAdapter;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<HorizontalRecyclerViewItem> listItems;
    private MyHorizontalAdapter adapter;
    private MyHorizontalPictureAdapter myOtherAdapter;
    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
    boolean isTablet;


    RecyclerView recyclerView;
    RecyclerView editRecyclerView;
    private Toolbar mToolbar;


    // --------------
    // BASE METHODS
    // --------------

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
        mToolbar = result.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        setHasOptionsMenu(true);

        if (isTablet) {
            mToolbar.setVisibility(View.INVISIBLE);
        }

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

        inter = result.findViewById(R.id.pointsOfInterest);
        desc = result.findViewById(R.id.description);
        surf = result.findViewById(R.id.surface);
        roo = result.findViewById(R.id.nbrOfRooms);
        bed = result.findViewById(R.id.nbrOfBedrooms);
        bath = result.findViewById(R.id.nbrOfBathrooms);
        add = result.findViewById(R.id.address);
        media = result.findViewById(R.id.media);

        realtorET = result.findViewById(R.id.realEstateInChargeET);
        descriptionET = result.findViewById(R.id.descriptionET);
        surfaceET = result.findViewById(R.id.surfaceET);
        addressET = result.findViewById(R.id.addressET);
        interestsET = result.findViewById(R.id.pointsOfInterestET);
        bathroomsET = result.findViewById(R.id.nbrOfBathroomsET);
        bedroomsET = result.findViewById(R.id.nbrOfBedroomsET);
        roomsET = result.findViewById(R.id.nbrOfRoomsET);
        realtorET.setVisibility(View.INVISIBLE);
        descriptionET.setVisibility(View.INVISIBLE);
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
        pointsOfInterest.setVisibility(View.INVISIBLE);
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
            id = mPrefs.getString("id", null);
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
                    ArrayList<String> pointsOfInterestValue = (ArrayList<String>) documentSnapshot.get("pointOfInterest ");
                    ArrayList<String> pictures = (ArrayList<String>) documentSnapshot.get("pictures");

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
                    }


                }
            }
            seeOnMapButton.setOnClickListener(v -> {
                Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                mapsIntent.putExtra("focus", houseItem.getAddress());
                startActivity(mapsIntent);
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
        recyclerView.setVisibility(View.INVISIBLE);
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

        mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.miniMapFrameLayout);

        if (mapsFragment == null) {
            mapsFragment = new MapsFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.miniMapFrameLayout, mapsFragment)
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (android.R.id.home):
                getActivity().onBackPressed();
                break;
            case R.id.navbar_edit:
                //item.setIcon(R.drawable.ic_location_on_black_24dp);
                //Intent addIntent = new Intent(getContext(), AddActivity.class);
                //startActivity(addIntent);
                pictureDescET.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                addAPicButton.setVisibility(View.VISIBLE);
                chosenPicIV.setVisibility(View.VISIBLE);
                realtorET.setVisibility(View.VISIBLE);
                descriptionET.setVisibility(View.VISIBLE);
                surfaceET.setVisibility(View.VISIBLE);
                addressET.setVisibility(View.VISIBLE);
                interestsET.setVisibility(View.VISIBLE);
                bathroomsET.setVisibility(View.VISIBLE);
                bedroomsET.setVisibility(View.VISIBLE);
                roomsET.setVisibility(View.VISIBLE);

                surface.setVisibility(View.INVISIBLE);
                rooms.setVisibility(View.INVISIBLE);
                bedrooms.setVisibility(View.INVISIBLE);
                bathrooms.setVisibility(View.INVISIBLE);
                address.setVisibility(View.INVISIBLE);
                description.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                sold.setVisibility(View.INVISIBLE);
                sold.setVisibility(View.INVISIBLE);
                realtor.setVisibility(View.INVISIBLE);
                market.setVisibility(View.INVISIBLE);
                pointsOfInterest.setVisibility(View.INVISIBLE);
                inter.setVisibility(View.INVISIBLE);
                seeOnMapButton.setText("UPDATE");

                addButton.setEnabled(false);
                addButton.setOnClickListener(v -> {
                    listItems.add(new HorizontalRecyclerViewItem(mainImageUri.toString(), pictureDescET.getText().toString()));
                    myOtherAdapter = new MyHorizontalPictureAdapter(getContext(), listItems);
                    editRecyclerView.setAdapter(myOtherAdapter);
                    pictureDescET.setText("");
                    chosenPicIV.setImageResource(0);

                    seeOnMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("house").document();

                            Map<String, Object> dataToSave = new HashMap<>();

                            dataToSave.put("description", descriptionET.getText().toString());
                            dataToSave.put("numberOfBathrooms", String.valueOf(bathroomsET.getText().toString()));
                            dataToSave.put("numberOfBedrooms", String.valueOf(bedroomsET.getText().toString()));
                            dataToSave.put("numberOfRooms", String.valueOf(rooms.getText().toString()));
                            dataToSave.put("pictures", );
                            dataToSave.put("rooms", arrayOfDesc);

                            if (mPrefs.getStringSet("interests", null) != null) {
                                List<String> newArray = new ArrayList<>(mPrefs.getStringSet("interests", null));
                                dataToSave.put("pointOfInterest", newArray);
                                mPrefs.edit().putStringSet("interests", null).apply();
                            } else {
                                dataToSave.put("pointOfInterest", interestsArray);
                            }
                            dataToSave.put("price", priceET.getText().toString());
                            dataToSave.put("surface", surfaceET.getText().toString());
                            dataToSave.put("type", typeET.getText().toString());
                            dataToSave.put("realtor", mPrefs.getString("username", "Realtor"));

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = new Date();

                            dataToSave.put("onMarket", dateFormat.format(date));


                            mDocRef.set(dataToSave, SetOptions.merge());
                        }
                    });
                });

                addAPicButton.setOnClickListener(v -> {
                    Intent myIntent = new Intent();
                    myIntent.setType("image/*");
                    myIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(myIntent, PICK_IMAGE_REQUEST);
                });
                break;
        }
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.navbar_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainImageUri = data.getData();

            chosenPicIV.setVisibility(View.VISIBLE);
            Picasso.get().load(mainImageUri).into(chosenPicIV);
            addAPicButton.setText("Change picture");
            addButton.setEnabled(true);
        }
    }
}
