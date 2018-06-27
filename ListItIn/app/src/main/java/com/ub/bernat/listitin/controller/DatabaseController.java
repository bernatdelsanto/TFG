package com.ub.bernat.listitin.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ub.bernat.listitin.PlaceListsAdapter;
import com.ub.bernat.listitin.model.Place;
import com.ub.bernat.listitin.model.PlaceList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
/*
* Class to handle different processes regarding Firebase's database. It also links data with adapter
 */
public class DatabaseController {
    DatabaseReference databaseReference;
    public DatabaseController(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void deleteList(String listID){
        final String id = listID;
        databaseReference.child("lists").child(listID).removeValue();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    if(dataSnapshot.child("created_lists").hasChild(id)){
                        dataSnapshot.getRef().child("created_lists").child(id).setValue(null);
                    }
                    if(dataSnapshot.child("following_lists").hasChild(id)){
                        dataSnapshot.getRef().child("following_lists").child(id).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", databaseError.getMessage());
            }
        });
    }

    public void addPlaceListToRecyclerViewById(final RecyclerView recyclerView, final String listID, final String listName){

        final ArrayList<Place> places = new ArrayList<>();

        databaseReference.child("lists").child(listID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = null;
                if(dataSnapshot.hasChild("l")) {
                    DataSnapshot location = dataSnapshot.child("l");
                    Double latitudeD = location.child("0").getValue(Double.class);
                    Float latitude = latitudeD.floatValue();
                    Double longitudeD = location.child("1").getValue(Double.class);
                    Float longitude = longitudeD.floatValue();
                    String name = dataSnapshot.child("name").getValue(String.class);
                    place = new Place(dataSnapshot.getKey(), name, latitude, longitude);
                    places.add(place);
                }
                PlaceList placeList = new PlaceList(listName,listID,places);

                PlaceListsAdapter oldAdapter =(PlaceListsAdapter)recyclerView.getAdapter();
                ArrayList<PlaceList> listOfLists = new ArrayList<>((ArrayList<PlaceList>)oldAdapter.getList());
                Boolean newList = true;
                for(PlaceList p : listOfLists){
                    if(p.getId().equals(listID)){
                        p=placeList;//TODO: Pot donar problemes si s'inicialitza desplegat perquè la llibreria Expandablelist no deixa canviar la llista del adapter a posteriori.
                        newList=false;
                        break;
                    }
                }
                if(newList){
                    listOfLists.add(placeList);
                    PlaceListsAdapter newAdapter = new PlaceListsAdapter(listOfLists,oldAdapter.getPermission(),recyclerView);
                    recyclerView.setAdapter(newAdapter);
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void deletePlaceFromList(String placeID, String listID) {
        databaseReference.child("lists").child(listID).child(placeID).removeValue();
    }

    public void followList(String id, String name) {
        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("users").child(uID).child("following_lists").child(id).setValue(name);
    }

    public void unfollowList(String id) {
        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("users").child(uID).child("following_lists").child(id).setValue(null);
    }
}
