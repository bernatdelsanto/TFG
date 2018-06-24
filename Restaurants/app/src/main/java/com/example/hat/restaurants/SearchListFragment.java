package com.example.hat.restaurants;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.example.hat.restaurants.model.Place;
import com.example.hat.restaurants.model.PlaceList;
import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<PlaceList> searchResults = new ArrayList<>();
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private double STRING_DISTANCE = 0.95;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchListFragment newInstance(int columnCount) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchlist_list, container, false);
        getActivity().setTitle("Search List");
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboardFrom(getContext(),getView());
                searchResults.clear();
                searchList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PlaceList item);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchList(String listName){
        final String name = listName;
        final MyListsAdapter adapter = new MyListsAdapter(searchResults, MyListsAdapter.PERMISSION_SEARCH,recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //TODO IMPORTANT: Check if the user is following this one etc. Maybe check in the adapter?
        databaseReference.child("lists").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild("name")){
                    ArrayList<Place> places = new ArrayList<>();
                    String listName = dataSnapshot.child("name").getValue(String.class);
                    listName.toLowerCase().trim();
                    JaroWinklerDistance comparator = new JaroWinklerDistance();
                    Double distance = comparator.apply(listName.toLowerCase().trim(),name.toLowerCase().trim());
                     if(distance>=STRING_DISTANCE){
                        for(DataSnapshot snap:dataSnapshot.getChildren()) {
                            Place place = null;
                            if (snap.hasChild("l")) {
                                DataSnapshot location = snap.child("l");
                                Double latitudeD = location.child("0").getValue(Double.class);
                                Float latitude = latitudeD.floatValue();
                                Double longitudeD = location.child("1").getValue(Double.class);
                                Float longitude = longitudeD.floatValue();
                                String name = snap.child("name").getValue(String.class);
                                place = new Place(snap.getKey(), name, latitude, longitude);
                                places.add(place);
                            }
                        }
                        //TODO: order places by JAROWINKLER distance
                        PlaceList placeList = new PlaceList(listName,dataSnapshot.getKey(),places);

                        MyListsAdapter oldAdapter =(MyListsAdapter)recyclerView.getAdapter();
                        ArrayList<PlaceList> listOfLists = new ArrayList<>((ArrayList<PlaceList>)oldAdapter.getList());

                        listOfLists.add(placeList);
                        MyListsAdapter newAdapter = new MyListsAdapter(listOfLists,oldAdapter.getPermission(),recyclerView);
                        recyclerView.setAdapter(newAdapter);



                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String listID = dataSnapshot.getKey();
                for(PlaceList p : searchResults){
                    if(p.getId()==listID){
                        searchResults.remove(p);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         /*databaseReference.child("lists").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//TODO: this only works when added, not on delete or modify (low priority)
                 for(DataSnapshot s : dataSnapshot.getChildren()){
                     String listName = s.child("name").getValue(String.class);
                     if(listName.equals(name)){
                         PlaceList place = new PlaceList (name,s.getKey());
                         if(!searchResults.contains(place)){
                             searchResults.add(place);
                             adapter.notifyDataSetChanged();
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });*/

    }
}
