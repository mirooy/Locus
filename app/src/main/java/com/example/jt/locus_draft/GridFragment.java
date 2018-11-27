package com.example.jt.locus_draft;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Location mLocation;
    private DatabaseReference mLocsRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Photo> mPhotos = new ArrayList<Photo>(30);

    public GridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridFragment newInstance(Location location) {
        GridFragment fragment = new GridFragment();
        fragment.mLocation = location;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_grid, container, false);

        //load recycler view
        mRecyclerView = v.findViewById(R.id.grid_images);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //open firebase ref
        mLocsRef = FirebaseDatabase.getInstance().getReference().child("locs");

        setLocsRefListener();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setLocsRefListener() {
        mLocsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //get data for each location
                HashMap<String, String> locData = (HashMap<String, String>) dataSnapshot.getValue(); //TODO change to custom location class

                double lat = Double.parseDouble(locData.get("lat"));
                double lng = Double.parseDouble(locData.get("lng"));
                Location photoLoc = new Location(LocationManager.GPS_PROVIDER);
                photoLoc.setLatitude(lat);
                photoLoc.setLongitude(lng);

                PhotoLoc pl = new PhotoLoc(locData.get("name"), lat, lng, locData.get("img"), dataSnapshot.getKey());

                //add photo if location is within certain radius of current location
//                System.out.println(photoLoc.distanceTo(mLocation));
//                System.out.println(mLocation);
//                System.out.println(photoLoc);
//                System.out.println(locData.get("img"));
                if (photoLoc.distanceTo(mLocation) < 1000) {
                    for (String img: locData.get("img").split(",")) {
                        mPhotos.add(new Photo(img, pl));
                    }
                    setAdapterAndUpdateData();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //nothing
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //nothing
            }
        });
    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mPhotos array
        // this will "refresh" our recycler view
        mAdapter = new GridAdapter(getActivity(), mPhotos, mRecyclerView, mLocation);
        mRecyclerView.setAdapter(mAdapter);
    }
}