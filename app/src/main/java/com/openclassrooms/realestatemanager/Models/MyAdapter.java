package com.openclassrooms.realestatemanager.Models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Activities.DetailActivity;
import com.openclassrooms.realestatemanager.Activities.MainActivity;
import com.openclassrooms.realestatemanager.Fragments.DetailFragment;
import com.openclassrooms.realestatemanager.Fragments.MainFragment;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<RecyclerViewItem> listItems;
    private Context context;
    private DetailFragment detailFragment;
    private MainFragment mainFragment;
    public FrameLayout detailLayout;
    public MainActivity mainActivity;



    public MyAdapter(List<RecyclerViewItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final RecyclerViewItem listItem = listItems.get(i);

       Picasso.get().load(listItem.getPicture()).into(viewHolder.recycleImageView);

        viewHolder.recycleType.setText(listItem.getType());
        viewHolder.recycleLocation.setText(listItem.getLocation());
        viewHolder.recyclePrice.setText(listItem.getPrice());


        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * detailFragment is null so goes to else statement but even if detailFrag.uTV(); gets called it crashes because detailFragment is null
                 *
                 * Probably need to find a way like in MainActivity or DEtailActivity where detailFragment takes a value but the conversion for the adapter is weird.
                 */

           //     Check if DetailFragment is visible (Tablet)
                if (detailFragment != null && detailFragment.isVisible()) {
                    //TABLET : Update directly TextView
                    detailFragment.updateTextView();
                } else {
                    //SMARTPHONE : Pass tag to the new intent that will show DetailActivity (and so DetailFragment)
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("id", listItem.getId());
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView recycleImageView;
        public TextView recycleLocation;
        public TextView recyclePrice;
        public TextView recycleType;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recycleImageView = itemView.findViewById(R.id.recyclerViewPicture);
            recycleLocation = itemView.findViewById(R.id.recyclerViewLocationTV);
            recycleType = itemView.findViewById(R.id.recyclerViewTypeTV);
            recyclePrice = itemView.findViewById(R.id.recyclerViewPriceTV);
            relativeLayout = itemView.findViewById(R.id.recyclerViewRelativeLayout);
            detailLayout = itemView.findViewById(R.id.frame_layout_detail);

        }
    }
}
