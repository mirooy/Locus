package com.example.jt.locus_draft;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PhotoLocationActivity extends AppCompatActivity {

    private final String SAVED_FILE = "saved.ser";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Photo> mPhotos = new ArrayList<Photo>(30);

    private TextView mLocationName;
    private TextView mDistance;

    private ImageButton saveButton;

    private PhotoLoc pl;
    private Location curLoc;
    private int mLocationNumber;

    private DatabaseReference mLocRef;
    private String mImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_location);

        Intent intent = getIntent();
        pl = (PhotoLoc) intent.getSerializableExtra("pl");
        curLoc = new Location(LocationManager.GPS_PROVIDER);
        curLoc.setLatitude(intent.getDoubleExtra("lat", 0));
        curLoc.setLongitude(intent.getDoubleExtra("lng", 0));

        mLocationNumber = intent.getIntExtra("ex", 0);

        //set up action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        saveButton = findViewById(R.id.save_button);

        mRecyclerView = findViewById(R.id.location_images);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mLocationName = findViewById(R.id.location_name);
        mDistance = findViewById(R.id.distance);

        mLocationName.setText(pl.name);
        Location photoLoc = new Location(LocationManager.GPS_PROVIDER);
        photoLoc.setLatitude(pl.lat);
        photoLoc.setLongitude(pl.lng);
        mDistance.setText(curLoc.distanceTo(photoLoc) + " meters away");


        setupSaveButton();

        mLocRef = FirebaseDatabase.getInstance().getReference("locs").child(pl.index +  "");

        setPhotos();
        setAdapterAndUpdateData();
    }

    private void setupSaveButton() {
        // update save button color
        ArrayList<PhotoLoc> pls = new ArrayList<PhotoLoc>();

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(SAVED_FILE);
            ois = new ObjectInputStream(fis);
            pls.addAll((ArrayList<PhotoLoc>) ois.readObject());
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pls.contains(pl)) {
            saveButton.setBackgroundTintList(getResources().getColorStateList(R.color.darkRed));
        }
        else {
            saveButton.setBackgroundTintList(getResources().getColorStateList(R.color.mediumGray));
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            ArrayList<PhotoLoc> pls = new ArrayList<PhotoLoc>();

            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = openFileInput(SAVED_FILE);
                ois = new ObjectInputStream(fis);
                pls.addAll((ArrayList<PhotoLoc>) ois.readObject());
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pls.contains(pl)) {
                pls.remove(pl);
                saveButton.setBackgroundTintList(getResources().getColorStateList(R.color.mediumGray));
            }
            else {
                pls.add(pl);
                saveButton.setBackgroundTintList(getResources().getColorStateList(R.color.darkRed));
            }

            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = openFileOutput(SAVED_FILE, Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(pls);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void setPhotos() {
        //get photo url from locs/[#]/img
        mLocRef.child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //load the url into a new photo object to display
                for (String img: dataSnapshot.getValue(String.class).split(",")) {
                    mPhotos.add(new Photo(img, pl));
                }
                setAdapterAndUpdateData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // don't worry about it
            }
        });

    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mPhotos array
        // this will "refresh" our recycler view
        mAdapter = new PhotoAdapter(this, mPhotos);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
