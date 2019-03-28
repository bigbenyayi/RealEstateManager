package com.openclassrooms.realestatemanager.Models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Activities.DetailActivity;
import com.openclassrooms.realestatemanager.Fragments.DetailFragment;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<RecyclerWith3Items> listItems;
    private Context context;
    private DetailFragment detailFragment;
    List<View> itemViewList = new ArrayList<>();


    public MyAdapter(List<RecyclerWith3Items> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_layout, viewGroup, false);
        itemViewList.add(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final RecyclerWith3Items listItem = listItems.get(i);

        viewHolder.recyclerViewTypeTV.setText(listItem.getType());
        viewHolder.recyclerViewLocationTV.setText(listItem.getCity());
        viewHolder.recyclerViewPriceTV.setText(listItem.getPrice());
        Picasso.get().load(listItem.getPicture()).into(viewHolder.recyclerViewPicture);

        viewHolder.recyclerViewRelativeLayout.setOnClickListener(view -> {
           setClickAction(listItem.getId());

            for (View tempItemView : itemViewList) {
                TextView price = tempItemView.findViewById(R.id.recyclerViewPriceTV);

                if (itemViewList.get(viewHolder.getAdapterPosition()) == tempItemView) {
                    tempItemView.setBackgroundColor(Color.parseColor("#b380ff"));
                    price.setTextColor(Color.parseColor("#FAFAFA"));
                } else {
                    tempItemView.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    price.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView recyclerViewTypeTV;
        TextView recyclerViewLocationTV;
        TextView recyclerViewPriceTV;
        ImageView recyclerViewPicture;
        RelativeLayout recyclerViewRelativeLayout;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            recyclerViewTypeTV = itemView.findViewById(R.id.recyclerViewTypeTV);
            recyclerViewLocationTV = itemView.findViewById(R.id.recyclerViewLocationTV);
            recyclerViewPriceTV = itemView.findViewById(R.id.recyclerViewPriceTV);
            recyclerViewPicture = itemView.findViewById(R.id.recyclerViewPicture);
            recyclerViewRelativeLayout = itemView.findViewById(R.id.recyclerViewRelativeLayout);


            ButterKnife.bind(this, itemView);

        }
    }

    void setClickAction(String id) {
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            configureAndShowDetailFragment();
        }
        SharedPreferences mPref = context.getSharedPreferences("SHARED", MODE_PRIVATE);

        //     Check if DetailFragment is visible (Tablet)
        if (detailFragment != null && detailFragment.isVisible()) {
            //TABLET : Update directly TextView
            mPref.edit().putString("id", id).apply();
            detailFragment.configureHorizontalRecyclerView();
            detailFragment.updateTextView();
            mPref.edit().putString("id", id).apply();


        } else {
            //SMARTPHONE : Passing tag to the new intent that will show DetailActivity (and so DetailFragment)
            Intent i = new Intent(context, DetailActivity.class);
            i.putExtra("id", id);
            mPref.edit().putString("id", id).apply();

//                    detailFragment.configureHorizontalRecyclerView();
            context.startActivity(i);
        }

    }

    public void configureAndShowDetailFragment() {
        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        detailFragment = (DetailFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null) {
            // Create new main fragment
            detailFragment = new DetailFragment();
            // Add it to FrameLayout container
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }


    }

}
