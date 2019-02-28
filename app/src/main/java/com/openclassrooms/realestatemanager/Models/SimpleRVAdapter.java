package com.openclassrooms.realestatemanager.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleRVAdapter extends RecyclerView.Adapter<SimpleRVAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<String> listItems;
    private Context mContext;

    // data is passed into the constructor
    public SimpleRVAdapter(Context context, List<String> listItems) {
        this.mInflater = LayoutInflater.from(context);
        this.listItems = listItems;
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public SimpleRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.interest_custom_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull SimpleRVAdapter.ViewHolder holder, int position) {
        final String listItem = listItems.get(position);

        holder.mTextView.setText(listItem);
        holder.mImageView.setOnClickListener(v -> {
            listItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listItems.size());
            SharedPreferences mPref = mContext.getSharedPreferences("SHARED", Context.MODE_PRIVATE);
            Set<String> set = new HashSet<>(listItems);

            mPref.edit().putStringSet("interests", set).apply();
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.interestCustomTV);
            mImageView = itemView.findViewById(R.id.deleteImage);
        }
    }
}