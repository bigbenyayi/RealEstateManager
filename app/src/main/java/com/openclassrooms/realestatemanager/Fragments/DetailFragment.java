package com.openclassrooms.realestatemanager.Fragments;


import android.content.Intent;
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

        return result;
    }

    public void updateTextView() {


        Intent iin = getActivity().getIntent();
        Bundle b = iin.getExtras();
        String id = null;
        DetailHouse houseItem = null;

        if (b != null) {
            id = (String) b.get("id");
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
        try {
            Picasso.get().load(houseItem.getPictures().getString(0)).into(picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
