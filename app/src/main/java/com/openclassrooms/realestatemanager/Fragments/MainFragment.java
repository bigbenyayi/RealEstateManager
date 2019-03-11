package com.openclassrooms.realestatemanager.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openclassrooms.realestatemanager.Activities.DetailActivity;
import com.openclassrooms.realestatemanager.Models.DialogBuilder;
import com.openclassrooms.realestatemanager.Models.RecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.SearchObject;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener, DialogBuilder.AlertDialogListener {

    // Declare callback
    private OnButtonClickedListener mCallback;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<RecyclerViewItem> listItems;
    List<RecyclerViewItem> housesList = new ArrayList<>();
    private FirestoreRecyclerAdapter<RecyclerViewItem, ProductViewHolder> theAdapter;
    SearchObject mSearchObject;

    private DetailFragment detailFragment;
    List<View> itemViewList = new ArrayList<>();



    // Declare our interface that will be implemented by any container activity
    public interface OnButtonClickedListener {
        public void onButtonClicked(View view);
    }

    // --------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout of MainFragment
        View result = inflater.inflate(R.layout.fragment_main, container, false);


        recyclerView = result.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("house");

        listItems = new ArrayList<>();
        RecyclerViewItem houseItem = null;

        FirestoreRecyclerOptions<RecyclerViewItem> options = new FirestoreRecyclerOptions.Builder<RecyclerViewItem>()
                .setQuery(query, RecyclerViewItem.class)
                .build();


        theAdapter = new FirestoreRecyclerAdapter<RecyclerViewItem, ProductViewHolder>(options) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_layout, viewGroup, false);
                itemViewList.add(view);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final RecyclerViewItem model) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                if (mSearchObject != null) {

                    if (mSearchObject.getCity().equalsIgnoreCase(model.getCity()) && mSearchObject.getCity() == null) {
                        if (mSearchObject.getRoomsMin() >= Integer.valueOf(model.getNumberOfRooms()) && Integer.valueOf(model.getNumberOfRooms()) >= mSearchObject.getRoomsMax()) {
                            if (model.getSold() != null && mSearchObject.isSold() || !mSearchObject.isSold() && !mSearchObject.isAvailable()) {
                                if (model.getSold() == null && mSearchObject.isAvailable() || !mSearchObject.isSold() && !mSearchObject.isAvailable()) {
                                    if (mSearchObject.getPhotosMin() >= model.getPictures().size() && model.getPictures().size() >= mSearchObject.getPhotosMax()) {
//                                        if (model.getPointOfInterest().contains("Park") == mSearchObject.isPark() &&
//                                                model.getPointOfInterest().contains("Restaurant") == mSearchObject.isRestaurant() &&
//                                                model.getPointOfInterest().contains("School") == mSearchObject.isSchool()) {
                                            if (mSearchObject.getSurfaceMin() >= Integer.valueOf(model.getSurface()) && Integer.valueOf(model.getSurface()) >= mSearchObject.getSurfaceMax()) {
                                                try {
                                                    if (format.parse(mSearchObject.getBeginDate()).after(format.parse(model.getOnMarket())) && format.parse(model.getOnMarket()).after(format.parse(mSearchObject.getEndDate())) || mSearchObject.getBeginDate() == null && mSearchObject.getEndDate() == null) {
                                                        holder.setPrice(model.getPrice());
                                                        holder.setQuickLocation(model.getCity());
                                                        holder.setType(model.getType());
                                                        holder.setPicture(model.getMainPicture());
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
//                                    }
                                }
                            }
                        }
                    }
                } else {
                    holder.setPrice(model.getPrice());
                    holder.setQuickLocation(model.getCity());
                    holder.setType(model.getType());
                    holder.setPicture(model.getMainPicture());
                }


                Log.d("addADebugger", model.getMainPicture() + "lol");

                holder.relativeLayout.setOnClickListener(v -> {
                    holder.setClickAction(model.getId());

                    for (View tempItemView : itemViewList) {
                        TextView price = tempItemView.findViewById(R.id.recyclerViewPriceTV);

                        if (itemViewList.get(holder.getAdapterPosition()) == tempItemView) {
                            tempItemView.setBackgroundColor(Color.parseColor("#b380ff"));
                            price.setTextColor(Color.parseColor("#FAFAFA"));
                        } else {
                            tempItemView.setBackgroundColor(Color.parseColor("#FAFAFA"));
                            price.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    }

                });
            }


        };
        recyclerView.setAdapter(theAdapter);

        return result;
    }

    @Override
    public void fetchData(String city, int roomsMin, int roomsMax, boolean sold, boolean available, String beginDate, String endDate, int photosMin, int photosMax, boolean park, boolean school, boolean restaurant, int surfaceMin, int surfaceMax) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        if (beginDate != null && endDate == null){
            endDate = dateFormat.format(date);
        }
        mSearchObject = new SearchObject(city, roomsMin, roomsMax, sold, available, beginDate, endDate, photosMin, photosMax, park, school, restaurant, surfaceMin, surfaceMax);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    // --------------
    // ACTIONS
    // --------------

    @Override
    public void onClick(View v) {
        // Spread the click to the parent activity
        mCallback.onButtonClicked(v);
    }

    @Override
    public void onStart() {
        super.onStart();
        theAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (theAdapter != null) {
            theAdapter.stopListening();
        }
    }

    // --------------
    // FRAGMENT SUPPORT
    // --------------

    // Create callback to parent activity
    private void createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public RelativeLayout relativeLayout;


        ProductViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            relativeLayout = itemView.findViewById(R.id.recyclerViewRelativeLayout);

        }

        void setPrice(String price) {
            TextView textView = view.findViewById(R.id.recyclerViewPriceTV);
            textView.setText(price);
        }

        void setQuickLocation(String location) {
            TextView textView = view.findViewById(R.id.recyclerViewLocationTV);
            textView.setText(location);
        }

        void setType(String type) {
            TextView textView = view.findViewById(R.id.recyclerViewTypeTV);
            textView.setText(type);
        }

        void setPicture(String picture) {
            ImageView imageViewRV = view.findViewById(R.id.recyclerViewPicture);
            Picasso.get().load(picture).into(imageViewRV);

        }

        void setClickAction(String id) {
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
            if (tabletSize) {
                configureAndShowDetailFragment();
            }
            SharedPreferences mPref = getActivity().getSharedPreferences("SHARED", MODE_PRIVATE);

            //     Check if DetailFragment is visible (Tablet)
            if (detailFragment != null && detailFragment.isVisible()) {
                //TABLET : Update directly TextView
                mPref.edit().putString("id", id).apply();
                detailFragment.configureHorizontalRecyclerView();
                detailFragment.updateTextView();

            } else {
                //SMARTPHONE : Passing tag to the new intent that will show DetailActivity (and so DetailFragment)
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("id", id);
                mPref.edit().putString("id", id).apply();

//                    detailFragment.configureHorizontalRecyclerView();
                startActivity(i);
            }

        }

    }

    public void configureAndShowDetailFragment() {
        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        detailFragment = (DetailFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null) {
            // Create new main fragment
            detailFragment = new DetailFragment();
            // Add it to FrameLayout container
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }


    }
}
