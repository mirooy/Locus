package com.example.jt.locus_draft;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SavedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<PhotoLoc> mSaved;
    private FrameLayout layout;
    private Location mLocation;

    private OnFragmentInteractionListener mListener;

    public SavedFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SavedFragment newInstance(Location loc) {
        SavedFragment fragment = new SavedFragment();
        fragment.setLocation(loc);
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
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        layout = (FrameLayout) view.findViewById(R.id.layout);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.saved);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.getContext().openFileInput("saved.ser");
            ois = new ObjectInputStream(fis);
            mSaved = (ArrayList<PhotoLoc>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = new SavedAdapter(getActivity(), mSaved, mRecyclerView, mLocation);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void setLocation(Location loc) {
        mLocation = loc;
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
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.getContext().openFileInput("saved.ser");
            ois = new ObjectInputStream(fis);
            mSaved = (ArrayList<PhotoLoc>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = new SavedAdapter(getActivity(), mSaved, mRecyclerView, mLocation);
        mRecyclerView.setAdapter(mAdapter);
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
        void onFragmentInteraction(Uri uri);
    }
}
