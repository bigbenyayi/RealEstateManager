package com.openclassrooms.realestatemanager.Models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.ETC1;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyHorizontalPictureAdapter extends RecyclerView.Adapter<MyHorizontalPictureAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<HorizontalRecyclerViewItem> listItems;
    private ArrayList<String> photoItems = new ArrayList<>();
    private ArrayList<String> descItems = new ArrayList<>();
    public static final int PICK_IMAGE_REQUEST = 1;
    Context mContext;
    Set<String> set = new HashSet<>();
    Set<String> setDesc = new HashSet<>();


    // data is passed into the constructor
    public MyHorizontalPictureAdapter(Context context, List<HorizontalRecyclerViewItem> listItems) {
        this.mInflater = LayoutInflater.from(context);
        this.listItems = listItems;
        mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.horizontal_image_custom_layout, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HorizontalRecyclerViewItem listItem = listItems.get(position);

        Picasso.get().load(listItem.getPictureUrl()).into(holder.mImageView);
        holder.mEditText.setText(listItem.getRoom());

        photoItems.add(listItem.getPictureUrl());
        descItems.add(listItem.getRoom());

        SharedPreferences mPref = mContext.getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        mPref.edit().putBoolean("boolean", false).apply();


        holder.mImageView.setOnClickListener(v -> {

            listItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listItems.size());
            photoItems.remove(position);
            notifyItemRangeChanged(position, photoItems.size());
            descItems.remove(position);
            notifyItemRangeChanged(position, descItems.size());

            set.clear();
            setDesc.clear();

            for (int i = 0; i < listItems.size(); i++) {

                set.add(listItems.get(i).getPictureUrl());
                setDesc.add(listItems.get(i).getRoom());

            }
            mPref.edit().putStringSet("pictures", set).apply();
            mPref.edit().putStringSet("description", setDesc).apply();
            mPref.edit().putBoolean("boolean", true).apply();

        });


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        EditText mEditText;
        ImageView mUslessImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.horizontalImageRecyclerViewIV);
            mEditText = itemView.findViewById(R.id.horizontalEditTextRecyclerViewIV);
            mUslessImageView = itemView.findViewById(R.id.horizontalImageRecyclerViewIVCross);
        }
    }


}