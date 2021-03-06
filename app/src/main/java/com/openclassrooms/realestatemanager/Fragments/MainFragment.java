package com.openclassrooms.realestatemanager.Fragments;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openclassrooms.realestatemanager.Activities.DetailActivity;
import com.openclassrooms.realestatemanager.Models.DatabaseHouseItem;
import com.openclassrooms.realestatemanager.Models.MyAdapter;
import com.openclassrooms.realestatemanager.Models.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Models.RecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.RecyclerWith3Items;
import com.openclassrooms.realestatemanager.Models.SearchObject;
import com.openclassrooms.realestatemanager.Models.Utils;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener {

    // Declare callback
    private OnButtonClickedListener mCallback;

    private RealEstateManagerDatabase database;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<RecyclerViewItem> listItems;
    List<RecyclerViewItem> housesList = new ArrayList<>();
    private FirestoreRecyclerAdapter<RecyclerViewItem, ProductViewHolder> theAdapter;
    SearchObject mSearchObject;
    ArrayList<DatabaseHouseItem> forDatabase = new ArrayList<>();
    int sizeOffline;


    private DetailFragment detailFragment;
    List<View> itemViewList = new ArrayList<>();


    // Declare our interface that will be implemented by any container activity
    public interface OnButtonClickedListener {
        public void onButtonClicked(View view);
    }

    // --------------
    private List<RecyclerViewItem> offlineListForAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout of MainFragment
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", MODE_PRIVATE);

        mSearchObject = new SearchObject(mPrefs.getString("city", null),
                mPrefs.getInt("roomsMin", 0),
                mPrefs.getInt("roomsMax", 80),
                mPrefs.getBoolean("sold", false),
                mPrefs.getBoolean("available", false),
                mPrefs.getString("beginDate", null),
                mPrefs.getString("endDate", null),
                mPrefs.getInt("photosMin", 0),
                mPrefs.getInt("photosMax", 20),
                mPrefs.getBoolean("park", false),
                mPrefs.getBoolean("school", false),
                mPrefs.getBoolean("restaurant", false),
                mPrefs.getInt("surfaceMin", 0),
                mPrefs.getInt("surfaceMax", 100000),
                mPrefs.getInt("priceMin", 0),
                mPrefs.getInt("priceMax", 100000000));

        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        recyclerView = result.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = Room.databaseBuilder(getContext(),
                RealEstateManagerDatabase.class, "MyDatabase.db")
                .allowMainThreadQueries()
                .build();

        if (Utils.isInternetAvailable(getContext())) {


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

                int size;

                @Override
                protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final RecyclerViewItem model) {

                    database.itemDao().insertItem(new DatabaseHouseItem(model.getDescription(), model.getSurface(), model.getId(), model.getNumberOfRooms(), model.getNumberOfBedrooms(),
                            model.getNumberOfBathrooms(), model.getLocation(), model.getRealtor(), model.getOnMarket(), model.getSold(), model.getMainPicture(),
                            model.getPrice(), model.getCity(), model.getType(), model.getPointOfInterest(), model.getPictures(), model.getRooms()));

                    //Loading all the pictures when online so that they can be displayed offline
                    if (model.getPictures() != null) {
                        for (int i = 0; i < model.getPictures().size(); i++)
                            Picasso.get().load(model.getPictures().get(i)).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                    }

                    if (mSearchObject.getRoomsMax() != 80) {
                        if (model.getPictures() == null) {
                            size = 0;
                        } else {
                            size = model.getPictures().size();
                        }
                        if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() != null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                        } else if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() == null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                            mSearchObject.setBeginDate("01/01/1900");
                        }

                        ViewGroup.LayoutParams layoutParams = holder.relativeLayout.getLayoutParams();
                        layoutParams.height = 0;
                        holder.relativeLayout.setLayoutParams(layoutParams);

                        if (model.getSurface().equals("")) {
                            model.setSurface("0");
                        }
                        if (model.getSold() == null) {
                            model.setSold("");
                        }

                        Log.d("thegreatdebugger", "1st");
                        if (mSearchObject.getCity() == null || mSearchObject.getCity().equalsIgnoreCase(model.getCity()) || mSearchObject.getCity().equals("")) {
                            Log.d("thegreatdebugger", "2nd");
                            if (mSearchObject.getRoomsMax() >= Integer.valueOf(model.getNumberOfRooms()) && Integer.valueOf(model.getNumberOfRooms()) >= mSearchObject.getRoomsMin()) {
                                Log.d("thegreatdebugger", "3rde " + model.getCity());
                                if (model.getSold().equals("") && mSearchObject.isAvailable() || !(model.getSold().equals("")) && mSearchObject.isSold() || !mSearchObject.isSold() && !mSearchObject.isAvailable() || mSearchObject.isSold() && mSearchObject.isAvailable()) {
                                    Log.d("thegreatdebugger", "4the " + model.getCity());
                                    if (mSearchObject.getPhotosMax() >= size && size >= mSearchObject.getPhotosMin()) {
                                        Log.d("thegreatdebugger", "6the " + model.getCity());
                                        if (mSearchObject.getPriceMax() >= Integer.valueOf(model.getPrice()) && Integer.valueOf(model.getPrice()) >= mSearchObject.getPriceMin()) {
                                            Log.d("thegreatdebugger", "7the " + model.getCity());
                                            if (!mSearchObject.isPark() || model.getPointOfInterest().contains("Park")) {
                                                Log.d("thegreatdebugger", "8the " + model.getCity());
                                                if (!mSearchObject.isSchool() || model.getPointOfInterest().contains("School")) {
                                                    Log.d("thegreatdebugger", "9the " + model.getCity());
                                                    if (!mSearchObject.isRestaurant() || model.getPointOfInterest().contains("Restaurant")) {
                                                        Log.d("thegreatdebugger", "10the " + model.getCity());
                                                        if (mSearchObject.getSurfaceMax() >= Integer.valueOf(model.getSurface().replace("m²", "")) && Integer.valueOf(model.getSurface().replace("m²", "")) >= mSearchObject.getSurfaceMin()) {
                                                            Log.d("thegreatdebugger", "11the " + model.getCity());
                                                            try {
                                                                Date beginDate = format.parse(mSearchObject.getBeginDate());
                                                                Date theDate = format.parse(model.getOnMarket());
                                                                Date endDate = format.parse(mSearchObject.getEndDate());
                                                                if (beginDate.before(theDate) && endDate.after(theDate) || beginDate.getTime() == theDate.getTime() && endDate.getTime() == theDate.getTime()) {
                                                                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                                                    holder.relativeLayout.setLayoutParams(layoutParams);
                                                                    Log.d("thegreatdebugger", "12the " + model.getCity());

                                                                    String priceWithComas = String.format("%,d", Integer.valueOf(model.getPrice()));
                                                                    holder.setPrice(priceWithComas + "$");
                                                                    holder.setQuickLocation(model.getCity());
                                                                    holder.setType(model.getType());
                                                                    holder.setPicture(model.getMainPicture());

                                                                }
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (model.getPrice() != null) {
                            String priceWithComas = null;
                            try {
                                priceWithComas = String.format("%,d", Integer.valueOf(model.getPrice()));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            holder.setPrice(priceWithComas + "$");

                        }
//                    String priceWithComas = "lalala";
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

        } else {
            Toast.makeText(getContext(), "Offline", Toast.LENGTH_SHORT).show();
            List<RecyclerWith3Items> list3items = new ArrayList<>();

            List<DatabaseHouseItem> listOfItems = database.itemDao().getItems();
            if (listOfItems != null) {
                for (int i = 0; i < listOfItems.size(); i++) {
                    DatabaseHouseItem fastGet = listOfItems.get(i);
                    if (mSearchObject.getRoomsMax() != 80) {
                        if (fastGet.getPictures() == null) {
                            sizeOffline = 0;
                        } else {
                            sizeOffline = fastGet.getPictures().size();
                        }
                        if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() != null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                        } else if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() == null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                            mSearchObject.setBeginDate("01/01/1900");
                        }
                    }
                    if (fastGet.getSaleDate() == null) {
                        fastGet.setSaleDate("");
                    }
                    if (mSearchObject.getCity() == null || mSearchObject.getCity().equalsIgnoreCase(fastGet.getCity()) || mSearchObject.getCity().equals("")) {
                        Log.d("thegreatdebugger", "2nd");
                        if (mSearchObject.getRoomsMax() >= Integer.valueOf(fastGet.getNbrOfRooms()) && Integer.valueOf(fastGet.getNbrOfRooms()) >= mSearchObject.getRoomsMin()) {
                            Log.d("thegreatdebugger", "3rd " + fastGet.getCity());
                            if (fastGet.getSaleDate().equals("") && mSearchObject.isAvailable() || !(fastGet.getSaleDate().equals("")) && mSearchObject.isSold() || !mSearchObject.isSold() && !mSearchObject.isAvailable() || mSearchObject.isSold() && mSearchObject.isAvailable()) {
                                Log.d("thegreatdebugger", "4th " + fastGet.getCity());
                                if (mSearchObject.getPhotosMax() >= sizeOffline && sizeOffline >= mSearchObject.getPhotosMin()) {
                                    Log.d("thegreatdebugger", "6th " + fastGet.getCity());
                                    if (mSearchObject.getPriceMax() >= Integer.valueOf(fastGet.getPrice()) && Integer.valueOf(fastGet.getPrice()) >= mSearchObject.getPriceMin()) {
                                        Log.d("thegreatdebugger", "7th " + fastGet.getCity());
                                        if (!mSearchObject.isPark() || fastGet.getPointsOfInterest().contains("Park")) {
                                            Log.d("thegreatdebugger", "8th " + fastGet.getCity());
                                            if (!mSearchObject.isSchool() || fastGet.getPointsOfInterest().contains("School")) {
                                                Log.d("thegreatdebugger", "9th " + fastGet.getCity());
                                                if (!mSearchObject.isRestaurant() || fastGet.getPointsOfInterest().contains("Restaurant")) {
                                                    Log.d("thegreatdebugger", "10th " + fastGet.getCity());
                                                    if (mSearchObject.getSurfaceMax() >= Integer.valueOf(fastGet.getSurface().replace("m²", "")) && Integer.valueOf(fastGet.getSurface().replace("m²", "")) >= mSearchObject.getSurfaceMin()) {
                                                        Log.d("thegreatdebugger", "11th " + fastGet.getCity());
                                                        try {
                                                            Date beginDate = format.parse(mSearchObject.getBeginDate());
                                                            Date theDate = format.parse(fastGet.getOnMarket());
                                                            Date endDate = format.parse(mSearchObject.getEndDate());
                                                            if (beginDate.before(theDate) && endDate.after(theDate) || beginDate.getTime() == theDate.getTime() && endDate.getTime() == theDate.getTime()) {

                                                                list3items.add(new RecyclerWith3Items(listOfItems.get(i).getId(), listOfItems.get(i).getType(),
                                                                        listOfItems.get(i).getCity(), listOfItems.get(i).getPrice(), listOfItems.get(i).getMainPicture()));

                                                                RecyclerView.Adapter offlineAdapter = new MyAdapter(list3items, getContext());
                                                                recyclerView.setAdapter(offlineAdapter);
                                                                offlineAdapter.notifyDataSetChanged();
                                                            }

                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }
        return result;

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
        if (theAdapter != null) {
            theAdapter.startListening();
        }
        if (!Utils.isInternetAvailable(Objects.requireNonNull(getContext()))) {
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            List<RecyclerWith3Items> list3items = new ArrayList<>();

            Toast.makeText(getContext(), "Offline start", Toast.LENGTH_SHORT).show();
            List<DatabaseHouseItem> listOfItems = database.itemDao().getItems();
            if (listOfItems != null) {
                for (int i = 0; i < listOfItems.size(); i++) {
                    DatabaseHouseItem fastGet = listOfItems.get(i);
                    if (mSearchObject.getRoomsMax() != 80) {
                        if (fastGet.getPictures() == null) {
                            sizeOffline = 0;
                        } else {
                            sizeOffline = fastGet.getPictures().size();
                        }
                        if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() != null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                        } else if (mSearchObject.getEndDate() == null && mSearchObject.getBeginDate() == null) {
                            Date date = new Date();
                            mSearchObject.setEndDate(format.format(date));
                            mSearchObject.setBeginDate("01/01/1900");
                        }

                        if (fastGet.getSaleDate() == null) {
                            fastGet.setSaleDate("");
                        }
                        if (mSearchObject.getCity() == null || mSearchObject.getCity().equalsIgnoreCase(fastGet.getCity()) || mSearchObject.getCity().equals("")) {
                            Log.d("thegreatdebugger", "2nds");
                            if (mSearchObject.getRoomsMax() >= Integer.valueOf(fastGet.getNbrOfRooms()) && Integer.valueOf(fastGet.getNbrOfRooms()) >= mSearchObject.getRoomsMin()) {
                                Log.d("thegreatdebugger", "3rds " + fastGet.getCity());
                                if (fastGet.getSaleDate().equals("") && mSearchObject.isAvailable() || !(fastGet.getSaleDate().equals("")) && mSearchObject.isSold() || !mSearchObject.isSold() && !mSearchObject.isAvailable() || mSearchObject.isSold() && mSearchObject.isAvailable()) {
                                    Log.d("thegreatdebugger", "4ths " + fastGet.getCity());
                                    if (mSearchObject.getPhotosMax() >= sizeOffline && sizeOffline >= mSearchObject.getPhotosMin()) {
                                        Log.d("thegreatdebugger", "6ths " + fastGet.getCity());
                                        if (mSearchObject.getPriceMax() >= Integer.valueOf(fastGet.getPrice()) && Integer.valueOf(fastGet.getPrice()) >= mSearchObject.getPriceMin()) {
                                            Log.d("thegreatdebugger", "7ths " + fastGet.getCity());
                                            if (!mSearchObject.isPark() || fastGet.getPointsOfInterest().contains("Park")) {
                                                Log.d("thegreatdebugger", "8ths " + fastGet.getCity());
                                                if (!mSearchObject.isSchool() || fastGet.getPointsOfInterest().contains("School")) {
                                                    Log.d("thegreatdebugger", "9ths " + fastGet.getCity());
                                                    if (!mSearchObject.isRestaurant() || fastGet.getPointsOfInterest().contains("Restaurant")) {
                                                        Log.d("thegreatdebugger", "10ths " + fastGet.getCity());
                                                        if (mSearchObject.getSurfaceMax() >= Integer.valueOf(fastGet.getSurface().replace("m²", "")) && Integer.valueOf(fastGet.getSurface().replace("m²", "")) >= mSearchObject.getSurfaceMin()) {
                                                            Log.d("thegreatdebugger", "11ths " + fastGet.getCity());

                                                            try {
                                                                Date beginDate = format.parse(mSearchObject.getBeginDate());
                                                                Date theDate = format.parse(fastGet.getOnMarket());
                                                                Date endDate = format.parse(mSearchObject.getEndDate());
                                                                if (beginDate.before(theDate) && endDate.after(theDate) || beginDate.getTime() == theDate.getTime() && endDate.getTime() == theDate.getTime()) {

                                                                    list3items.add(new RecyclerWith3Items(listOfItems.get(i).getId(), listOfItems.get(i).getType(),
                                                                            listOfItems.get(i).getCity(), listOfItems.get(i).getPrice(), listOfItems.get(i).getMainPicture()));

                                                                    RecyclerView.Adapter offlineAdapter = new MyAdapter(list3items, getContext());
                                                                    recyclerView.setAdapter(offlineAdapter);
                                                                    offlineAdapter.notifyDataSetChanged();
                                                                }

                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        list3items.add(new RecyclerWith3Items(listOfItems.get(i).getId(), listOfItems.get(i).getType(),
                                listOfItems.get(i).getCity(), fastGet.getPrice(), listOfItems.get(i).getMainPicture()));

                        RecyclerView.Adapter offlineAdapter = new MyAdapter(list3items, getContext());
                        recyclerView.setAdapter(offlineAdapter);
                        offlineAdapter.notifyDataSetChanged();

                    }
                }
            }
        }

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
            SharedPreferences mPref = Objects.requireNonNull(getActivity()).getSharedPreferences("SHARED", MODE_PRIVATE);

            //     Check if DetailFragment is visible (Tablet)
            if (detailFragment != null && detailFragment.isVisible()) {
                //TABLET : Update directly TextView
                mPref.edit().putString("id", id).apply();
                detailFragment.configureHorizontalRecyclerView();
                detailFragment.updateTextView();
                mPref.edit().putString("id", id).apply();


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
        detailFragment = (DetailFragment) ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

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
