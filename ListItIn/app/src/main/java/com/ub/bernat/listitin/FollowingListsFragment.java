package com.ub.bernat.listitin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ub.bernat.listitin.controller.DatabaseController;
import com.ub.bernat.listitin.model.PlaceList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingListsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference databaseReference;

    private ArrayList<PlaceList> placeList = new ArrayList<>();
    private RecyclerView recyclerView;
    FloatingActionButton addListButton;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FollowingListsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListsFragment newInstance(String param1, String param2) {
        MyListsFragment fragment = new MyListsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_lists, container, false);
        getActivity().setTitle("Following Lists");
        addListButton = view.findViewById(R.id.addListButton);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchListFragment searchListFragment = new SearchListFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout,searchListFragment).commit();
                NavigationView nav_search = getActivity().findViewById(R.id.nav_view);
                nav_search.setCheckedItem(R.id.nav_searchlist);
            }
        });
        recyclerView = view.findViewById(R.id.listsView);
        final PlaceListsAdapter adapter = new PlaceListsAdapter(placeList, PlaceListsAdapter.PERMISSION_FOLLOW,recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following_lists");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String listName = dataSnapshot.getValue(String.class);
                String listID = dataSnapshot.getKey();
                DatabaseController controller= new DatabaseController();
                controller.addPlaceListToRecyclerViewById(recyclerView,listID,listName);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String listID = dataSnapshot.getKey();
                PlaceListsAdapter adapter = (PlaceListsAdapter) recyclerView.getAdapter();
                if(adapter != null) {
                    ArrayList<PlaceList> listOfLists = (ArrayList<PlaceList>) adapter.getList();
                    for (PlaceList p : listOfLists) {
                        if (p.getId().equals(listID)) {
                            adapter.getGroups().remove(p);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
