package com.example.jt.locus_draft;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private MapView mMapView;
    private GoogleMap mMap;

    private Location mLocation;

    private SearchView mSearchBar;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // add custom design to map
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            if (!success) {
                //do nothing
            }
        } catch (Resources.NotFoundException e) {
            //do nothing
        }

        LatLng berkeley = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        float zoomLevel = 15.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(berkeley, zoomLevel));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(berkeley));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        loadLocations();
        setOnCircleClickListener();
    }

    private void loadLocations() {
        DatabaseReference locsRef = FirebaseDatabase.getInstance().getReference().child("locs");

        //get all locations from firebase
        locsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //get values of location from database
                HashMap<String, String> locData = (HashMap<String, String>) dataSnapshot.getValue();

                //add circle at given latitude and longitude
                double lat = Double.parseDouble(locData.get("lat"));
                double lng = Double.parseDouble(locData.get("lng"));

                PhotoLoc pl = new PhotoLoc(locData.get("name"), lat, lng, locData.get("img"), dataSnapshot.getKey());

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lng))
                        .radius(32)
                        .fillColor(Color.BLACK)
                        .clickable(true));

                circle.setClickable(true);
                circle.setTag(pl); //make a meaningful tag here
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

    private void setOnCircleClickListener() {
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                //do something with the tag here
                PhotoLoc pl = (PhotoLoc) circle.getTag();

                //send to location activity
                Intent intent = new Intent(getActivity(), PhotoLocationActivity.class);
                intent.putExtra("pl", pl);
                intent.putExtra("lat", mLocation.getLatitude());
                intent.putExtra("lng", mLocation.getLongitude());
                startActivity(intent);
            }
        });
    }

    private void setSearchOnClickListener() {
        mSearchBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    System.out.println("here");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeLocation(Place place) {
        //move map to new location
        System.out.println(place);
        LatLng newLoc = place.getLatLng();
        float zoomLevel = 15.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, zoomLevel));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));

        //send new location to parent activity for grid view
        sendLocationToParent(newLoc);
    }

    private void sendLocationToParent(LatLng latLng) {
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        mListener.onLocationChange(loc);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // give the place back to the running fragment (must be map fragment)
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                changeLocation(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //ignore
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MainFragment newInstance(Location loc) {
        MainFragment fragment = new MainFragment();
        fragment.setLocation(loc);
        return fragment;
    }

    private void setLocation(Location loc) {
        mLocation = loc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // code from https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        //set up search bar
        mSearchBar = v.findViewById(R.id.search);
        setSearchOnClickListener();

        return v;
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
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
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
        void onLocationChange(Location location);
    }
}
