package com.example.jt.locus_draft;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private ArrayList<PhotoLoc> mSaved;
    private Location mLocation;

    public SavedAdapter(Context context, ArrayList<PhotoLoc> saved, RecyclerView rv, Location loc) {
        mContext = context;
        mSaved = saved;
        mRecyclerView = rv;
        mLocation = loc;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_cell_layout, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mRecyclerView.getChildLayoutPosition(view);
                PhotoLoc pl = (PhotoLoc) mSaved.get(index);
                Intent intent = new Intent(mContext, PhotoLocationActivity.class);
                intent.putExtra("pl", pl);
                intent.putExtra("lat", mLocation.getLatitude());
                intent.putExtra("lng", mLocation.getLongitude());
                mContext.startActivity(intent);
            }
        });
        return new SavedViewHolder(view);
    }


    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // here, we the comment that should be displayed at index `position` in our recylcer view
        // everytime the recycler view is refreshed, this method is called getItemCount() times (because
        // it needs to recreate every cell).
        PhotoLoc saved = (PhotoLoc) mSaved.get(position);
        ((SavedViewHolder) holder).bind(saved);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSaved.size();
    }

}

class SavedViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public LinearLayout mSavedBubbleLayout;
    public ImageView mImageView;
    public TextView mNameTextView;

    public SavedViewHolder(View itemView) {
        super(itemView);
        mSavedBubbleLayout = itemView.findViewById(R.id.saved_cell_layout);
        mImageView = mSavedBubbleLayout.findViewById(R.id.saved_cell_image);
        mNameTextView = mSavedBubbleLayout.findViewById(R.id.saved_cell_name);
    }

    void bind(PhotoLoc saved) {
        mNameTextView.setText(saved.name);
        String img = saved.file.split(",")[0];
        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(img);

        // Load the image using Glide
        Glide.with(mSavedBubbleLayout.getContext())
            .using(new FirebaseImageLoader())
            .load(pathReference)
            .into(mImageView);
    }
}
