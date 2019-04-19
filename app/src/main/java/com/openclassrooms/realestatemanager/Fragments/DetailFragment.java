package com.openclassrooms.realestatemanager.Fragments;


import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.openclassrooms.realestatemanager.Activities.EditActivity;
import com.openclassrooms.realestatemanager.Activities.MapsActivity;
import com.openclassrooms.realestatemanager.Models.DatabaseHouseItem;
import com.openclassrooms.realestatemanager.Models.DetailHouse;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.MyHorizontalPictureAdapter;
import com.openclassrooms.realestatemanager.Models.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Models.Utils;
import com.openclassrooms.realestatemanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
    Boolean editing = false;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    List<String> pointsOfInterestValue = new ArrayList<>();

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

    FrameLayout fl;

    private RealEstateManagerDatabase database;

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

    RelativeLayout relativeLayout;


    RecyclerView recyclerView;


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

        isTablet = getResources().getBoolean(R.bool.isTablet);
        database = Room.databaseBuilder(getContext(),
                RealEstateManagerDatabase.class, "MyDatabase.db")
                .allowMainThreadQueries()
                .build();

        fetchingViewFromXML(result);
        setTheInvisibles();

        setHasOptionsMenu(true);

        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();

        if (Utils.isInternetAvailable(getContext())) {
            configureHorizontalRecyclerView();
            seeOnMapButton.setVisibility(View.VISIBLE);
            fl.setVisibility(View.VISIBLE);
            fl.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        } else {
            configureHorizontalRecyclerViewWhenOffline();
            seeOnMapButton.setVisibility(View.INVISIBLE);
            fl.setVisibility(View.INVISIBLE);
            fl.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }

        media.setText("Please select a house to see the details");

        if (b != null) {
            if (Utils.isInternetAvailable(getContext())) {
                updateTextView();
            } else {
                updateTextViewWhenOffline();
            }
        }
        return result;
    }

    private void fetchingViewFromXML(View result) {
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

        inter = result.findViewById(R.id.pointsOfInterest);
        desc = result.findViewById(R.id.description);
        surf = result.findViewById(R.id.surface);
        roo = result.findViewById(R.id.nbrOfRooms);
        bed = result.findViewById(R.id.nbrOfBedrooms);
        bath = result.findViewById(R.id.nbrOfBathrooms);
        add = result.findViewById(R.id.address);
        media = result.findViewById(R.id.media);
        fl = result.findViewById(R.id.miniMapFrameLayout);
        recyclerView = result.findViewById(R.id.horizontalRecyclerView);
        relativeLayout = result.findViewById(R.id.relativeLayoutInvisibility);

    }

    private void setTheInvisibles() {
        relativeLayout.setVisibility(View.INVISIBLE);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
    }

    public void updateTextViewWhenOffline() {

        Intent iin = Objects.requireNonNull(getActivity()).getIntent();
        Bundle b = iin.getExtras();
        String id;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", Context.MODE_PRIVATE);
            id = mPrefs.getString("id", "0");
        }

        List<DatabaseHouseItem> listOfItems = database.itemDao().getItems();

        for (int i = 0; i < listOfItems.size(); i++) {

            if (Objects.requireNonNull(id).equals(listOfItems.get(i).getId())) {

                String descriptionValue = listOfItems.get(i).getDescription();
                String surfaceValue = listOfItems.get(i).getSurface();
                String nbrOfRooms = listOfItems.get(i).getNbrOfRooms();
                String nbrOfBedrooms = listOfItems.get(i).getNbrOfBedrooms();
                String nbrOfBathrooms = listOfItems.get(i).getNbrOfBathrooms();
                String location = listOfItems.get(i).getLocation();
                String realtorValue = listOfItems.get(i).getRealtor();
                String onMarket = listOfItems.get(i).getOnMarket();
                String soldValue = listOfItems.get(i).getSaleDate();
                pointsOfInterestValue = listOfItems.get(i).getPointsOfInterest();
                List<String> pictures = listOfItems.get(i).getPictures();


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
                    for (int j = 0; j < houseItem.getPointsOfInterest().size(); j++) {

                        if (j == houseItem.getPointsOfInterest().size() - 1) {
                            pointsOfInterest.append(houseItem.getPointsOfInterest().get(j));
                        } else {
                            pointsOfInterest.append(houseItem.getPointsOfInterest().get(j) + ", ");

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

        desc.setText("Description");
        surf.setText("Surface");
        roo.setText("Rooms");
        bed.setText("Bedrooms");
        bath.setText("Bathrooms");
        add.setText("Address");
        media.setText("Media");

        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        media.setVisibility(View.VISIBLE);

        if (isTablet) {
            configureAndDisplayMiniMap();
        } else {
            if (Utils.isInternetAvailable(getContext())) {
                seeOnMapButton.setVisibility(View.VISIBLE);
            }
            fl.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));

        }
    }

    public void configureHorizontalRecyclerViewWhenOffline() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        listItems = new ArrayList<>();
        listItems.clear();


        List<DatabaseHouseItem> listOfItems = database.itemDao().getItems();
        for (int i = 0; i < listOfItems.size(); i++) {
            Intent iin = getActivity().getIntent();
            Bundle b = iin.getExtras();
            String id;

            if (b != null) {
                id = (String) b.get("id");
            } else {
                SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", Context.MODE_PRIVATE);
                id = mPrefs.getString("id", null);
            }

            assert id != null;
            if (id.equals(listOfItems.get(i).getId())) {

                List<String> pictures = listOfItems.get(i).getPictures();
                List<String> rooms = listOfItems.get(i).getRooms();

                for (int j = 0; j < pictures.size(); j++) {

                    photoItem = new HorizontalRecyclerViewItem(pictures.get(j), rooms.get(j));

                    listItems.add(photoItem);
                    photoItems.add(pictures.get(j));
                    roomItems.add(rooms.get(j));
                }


            }
        }

        adapter = new MyHorizontalAdapter(getContext(), listItems);
        recyclerView.setAdapter(adapter);
    }

    public void updateTextView() {

        Intent iin = Objects.requireNonNull(getActivity()).getIntent();
        Bundle b = iin.getExtras();
        String id;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", Context.MODE_PRIVATE);
            id = mPrefs.getString("id", "0");
        }

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                assert id != null;
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

        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        media.setVisibility(View.VISIBLE);

        if (isTablet) {
            configureAndDisplayMiniMap();
        } else {
            seeOnMapButton.setVisibility(View.VISIBLE);
            fl.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));

        }
    }

    public void configureHorizontalRecyclerView() {

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        listItems = new ArrayList<>();
        listItems.clear();

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Intent iin = Objects.requireNonNull(getActivity()).getIntent();
                Bundle b = iin.getExtras();
                String id;

                if (b != null) {
                    id = (String) b.get("id");
                } else {
                    SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", Context.MODE_PRIVATE);
                    id = mPrefs.getString("id", null);
                }

                assert id != null;
                if (id.equals(documentSnapshot.get("id"))) {

                    ArrayList<String> pictures = (ArrayList<String>) documentSnapshot.get("pictures");
                    ArrayList<String> rooms = (ArrayList<String>) documentSnapshot.get("rooms");

                    for (int j = 0; j < pictures.size(); j++) {

                        assert rooms != null;
                        photoItem = new HorizontalRecyclerViewItem(pictures.get(j), rooms.get(j));

                        listItems.add(photoItem);
                        photoItems.add(pictures.get(j));
                        roomItems.add(rooms.get(j));
                    }
                }
            }
            adapter = new MyHorizontalAdapter(getContext(), listItems);
            recyclerView.setAdapter(adapter);
        });


    }

    public void configureAndDisplayMiniMap() {

        SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", Context.MODE_PRIVATE);

        Intent iin = Objects.requireNonNull(getActivity()).getIntent();
        Bundle b = iin.getExtras();
        String id;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            id = mPrefs.getString("id", "0");
        }

        if (isTablet) {
            if (houseItem != null) {
                mPrefs.edit().putString("miniMapLocation", houseItem.getAddress()).apply();

                mapsFragment = (MapsFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.miniMapFrameLayout);
                if (mapsFragment == null) {
                    mapsFragment = new MapsFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.miniMapFrameLayout, mapsFragment)
                            .commit();
                }
            } else {
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        assert id != null;
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

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (android.R.id.home):
                Objects.requireNonNull(getActivity()).onBackPressed();
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
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.navbar_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
