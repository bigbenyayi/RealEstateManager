package com.openclassrooms.realestatemanager.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Activities.MainActivity;
import com.openclassrooms.realestatemanager.Models.DetailHouse;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyAdapter;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.RecyclerViewItem;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


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

    TextView inter;
    TextView desc;
    TextView surf;
    TextView roo;
    TextView bed;
    TextView bath;
    TextView add;
    TextView media;

    private List<HorizontalRecyclerViewItem> listItems;
    private MyHorizontalAdapter adapter;


    RecyclerView recyclerView;


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

        inter = result.findViewById(R.id.pointsOfInterest);
        desc = result.findViewById(R.id.description);
        surf = result.findViewById(R.id.surface);
        roo = result.findViewById(R.id.nbrOfRooms);
        bed = result.findViewById(R.id.nbrOfBedrooms);
        bath = result.findViewById(R.id.nbrOfBathrooms);
        add = result.findViewById(R.id.address);
        media = result.findViewById(R.id.media);

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
        DetailHouse houseItem = null;

        if (b != null) {
            id = (String) b.get("id");
        } else {
            SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
            id = mPrefs.getString("id", null);
        }

        String json;
        try {
            InputStream is = getContext().getAssets().open("houses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (id.equals(obj.getString("id"))) {
                    houseItem = new DetailHouse(obj.getString("description"), obj.getString("surface"), obj.getString("numberOfRooms"),
                            obj.getString("numberOfBedrooms"), obj.getString("numberOfBathrooms"), obj.getString("location"),
                            obj.getString("realtor"), obj.getString("onMarket"),
                            obj.getString("sold"), obj.getJSONArray("pointsOfInterest"), obj.getJSONArray("pictures"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //assert houseItem != null;
        description.setText(houseItem.getDescription());

        surface.setText(houseItem.getSurface() + "m²");
        rooms.setText(houseItem.getNbrOfRooms());
        bedrooms.setText(houseItem.getNbrOfBedrooms());
        bathrooms.setText(houseItem.getNbrOfBathrooms());
        address.setText(houseItem.getAddress());
        realtor.setText("Real Estate Agent: " + houseItem.getRealtor());
        market.setText("On market since: " + houseItem.getOnMarket());
        sold.setText("Sold: " + houseItem.getSaleDate());

        if (houseItem.getPointsOfInterest() != null) {
            pointsOfInterest.setText("");
            for (int i = 0; i < houseItem.getPointsOfInterest().length(); i++) {
                try {
                    if (i == houseItem.getPointsOfInterest().length() - 1) {
                        pointsOfInterest.append((CharSequence) houseItem.getPointsOfInterest().get(i));
                    } else {
                        pointsOfInterest.append(houseItem.getPointsOfInterest().get(i) + ", ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            pointsOfInterest.setVisibility(View.INVISIBLE);
            inter.setVisibility(View.INVISIBLE);
        }

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


    }

    public void configureHorizontalRecyclerView() {

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        listItems = new ArrayList<>();
        HorizontalRecyclerViewItem photoItem = null;
        String json;
        listItems.clear();
        try {
            InputStream is = getContext().getAssets().open("houses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // IF ID FROM INTENT OR SP IS THE SAME AS THE FOR LOOP ONE THEN WE ADD IT FOR EVERY PICTURE IN THE ARRAY

            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Intent iin = getActivity().getIntent();
                Bundle b = iin.getExtras();
                String id;

                if (b != null) {
                    id = (String) b.get("id");
                } else {
                    SharedPreferences mPrefs = getContext().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
                    id = mPrefs.getString("id", null);
                }

                if (obj.get("id").equals(id)) {

                    JSONArray picturesArray = obj.getJSONArray("pictures");
                    JSONArray roomsArray = obj.getJSONArray("rooms");

                    for (int j = 0; j < picturesArray.length(); j++) {

                        photoItem = new HorizontalRecyclerViewItem((String) picturesArray.get(j), (String) roomsArray.get(j));

                        listItems.add(photoItem);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new MyHorizontalAdapter(getContext(), listItems);

        recyclerView.setAdapter(adapter);
    }


}
