package com.example.jt.locus_draft;


import android.content.Context;
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

public class PhotoAdapter extends RecyclerView.Adapter {


    protected Context mContext;
    private ArrayList<Photo> mPhotos;

    public PhotoAdapter(Context context, ArrayList<Photo> landmarks) {
        mContext = context;
        mPhotos = landmarks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // here, we specify what kind of view each cell should have. In our case, all of them will have a view
        // made from comment_cell_layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_layout, parent, false);
        return new PhotoViewHolder(view);
    }


    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // here, we the comment that should be displayed at index `position` in our recycler view
        // every time the recycler view is refreshed, this method is called getItemCount() times (because
        // it needs to recreate every cell).
        Photo photo = mPhotos.get(position);
        ((PhotoViewHolder) holder).bind(photo, mContext);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

}

class PhotoViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case'
    public View mItemView;
    public LinearLayout mPhotoBubbleLayout;
    public ImageView mPhotoView;

    public PhotoViewHolder(View itemView) {
        super(itemView);

        mItemView = itemView;
        mPhotoBubbleLayout = mItemView.findViewById(R.id.photo_cell_layout);
        mPhotoView = mPhotoBubbleLayout.findViewById(R.id.photo);
    }

    void bind(Photo photo, Context context) {
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