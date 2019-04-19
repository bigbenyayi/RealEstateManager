package com.openclassrooms.realestatemanager.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Activities.EditActivity;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyHorizontalPictureAdapter extends RecyclerView.Adapter<MyHorizontalPictureAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<HorizontalRecyclerViewItem> listItems;
    private Context mContext;


    // data is passed into the constructor
    public MyHorizontalPictureAdapter(Context context, ArrayList<HorizontalRecyclerViewItem> listItems) {
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
        holder.mTextView.setText(listItem.getRoom());

        holder.mImageView.setOnClickListener(v -> {
            if(mContext instanceof EditActivity){
                ((EditActivity)mContext).adapterSendsList(position);
            }
        });
    }

    public ArrayList<HorizontalRecyclerViewItem> getUpdatedlist() {
        return listItems;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        ImageView mUslessImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.horizontalImageRecyclerViewIV);
            mTextView = itemView.findViewById(R.id.horizontalImageRecyclerViewTV);
            mUslessImageView = itemView.findViewById(R.id.horizontalImageRecyclerViewIVCross);
        }
    }


}