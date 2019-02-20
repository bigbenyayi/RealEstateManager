package com.openclassrooms.realestatemanager.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Models.DetailHouse;
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
    ImageView picture;

    TextView desc;
    TextView surf;
    TextView roo;
    TextView bed;
    TextView bath;
    TextView add;
    TextView media;

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

        description = result.findViewById(R.id.descriptionTV);
        surface = result.findViewById(R.id.surfaceTV);
        rooms = result.findViewById(R.id.nbrOfRoomsTV);
        bedrooms = result.findViewById(R.id.nbrOfBedroomsTV);
        bathrooms = result.findViewById(R.id.nbrOfBathroomsTV);
        address = result.findViewById(R.id.addressTV);
        picture = result.findViewById(R.id.pictures);

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
        media.setText("Please select a house to see the details");

        return result;
    }

    public void updateTextView() {


        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();
        String id = null;
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
                            obj.getString("numberOfBedrooms"), obj.getString("numberOfBathrooms"), obj.getString("location"), obj.getJSONArray("pictures"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assert houseItem != null;
        description.setText(houseItem.getDescription());
        surface.setText(houseItem.getSurface() + "m²");
        rooms.setText(houseItem.getNbrOfRooms());
        bedrooms.setText(houseItem.getNbrOfBedrooms());
        bathrooms.setText(houseItem.getNbrOfBathrooms());
        address.setText(houseItem.getAddress());

        desc.setText("Description");
        surf.setText("Surface");
        roo.setText("Rooms");
        bed.setText("Bedrooms");
        bath.setText("Bathrooms");
        add.setText("Address");
        media.setText("Media");
        try {
            Picasso.get().load(houseItem.getPictures().getString(0)).into(picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        surface.setVisibility(View.VISIBLE);
        rooms.setVisibility(View.VISIBLE);
        bedrooms.setVisibility(View.VISIBLE);
        bathrooms.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        surf.setVisibility(View.VISIBLE);
        roo.setVisibility(View.VISIBLE);
        bed.setVisibility(View.VISIBLE);
        bath.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        media.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);


    }
}
