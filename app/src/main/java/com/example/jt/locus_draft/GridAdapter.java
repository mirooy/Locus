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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by User on 5/1/2018.
 */

public class GridAdapter extends RecyclerView.Adapter {

    private RecyclerView mRecyclerView;
    protected Context mContext;
    private ArrayList<Photo> mPhotos;
    private Location mLocation;

    public GridAdapter(Context context, ArrayList<Photo> landmarks, RecyclerView rv, Location loc) {
        mContext = context;
        mPhotos = landmarks;
        mRecyclerView = rv;
        mLocation = loc;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_layout, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mRecyclerView.getChildLayoutPosition(view);
                PhotoLoc pl = mPhotos.get(index).pl;
                Intent intent = new Intent(mContext, PhotoLocationActivity.class);
                intent.putExtra("pl", pl);
                intent.putExtra("lat", mLocation.getLatitude());
                intent.putExtra("lng", mLocation.getLongitude());
                mContext.startActivity(intent);
            }
        });
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Photo photo = mPhotos.get(position);
        ((GridViewHolder) holder).bind(photo, mContext);
    }

    @Override
    public int getItemCount() { return mPhotos.size(); }

    class GridViewHolder extends RecyclerView.ViewHolder {

        public View mItemView;
        public LinearLayout mPhotoBubbleLayout;
        public ImageView mPhotoView;

        public GridViewHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
            mPhotoBubbleLayout = mItemView.findViewById(R.id.photo_cell_layout);
            mPhotoView = mPhotoBubbleLayout.findViewById(R.id.photo);
        }

        public void bind(Photo photo, Context context) {
            //use firebase and bind photoview to the image url
            // Reference to an image file in Firebase Storage
            StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(photo.getImgUrl());

            // Load the image using Glide
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .into(mPhotoView);
        }

    }
}