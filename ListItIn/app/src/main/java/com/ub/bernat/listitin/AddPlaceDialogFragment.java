package com.ub.bernat.listitin;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.ub.bernat.listitin.model.Place;
import com.ub.bernat.listitin.model.PlaceList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * DialogFragment for adding place to a list
 */
public class AddPlaceDialogFragment extends AppCompatDialogFragment {
    private Button addButton;
    private Button cancelButton;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;

    private ArrayList<PlaceList> placeList = new ArrayList<>();

    public interface OnInputSelected{
        void sendInput(ArrayList<String> listsIds);
    }
    public OnInputSelected onInputSelected;
    public AddPlaceDialogFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "AddPlaceDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_place_dialog, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarDialog);
        addButton = view.findViewById(R.id.addListButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        recyclerView = view.findViewById(R.id.recyclerViewAddPlace);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(placeList,getContext()); //Implement basic (non expandable) adapter for the checklist.

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("created_lists");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String listName = dataSnapshot.getValue(String.class);
                String listID = dataSnapshot.getKey();
                PlaceList thisPlace = new PlaceList(listName,listID,new ArrayList<Place>());
                placeList.add(thisPlace); //TODO: from place id, only show the lists that don't have this place.

                adapter.notifyDataSetChanged();
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> checkedLists = new ArrayList<>();
                for (int i =0; i<recyclerView.getChildCount();i++){
                    RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if(holder.checkbox.isChecked()){
                        checkedLists.add(holder.placeID);
                    }
                }
                onInputSelected.sendInput(checkedLists);
                dismiss();
            }
        });
        return view;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onInputSelected = (OnInputSelected)getTargetFragment();
        }catch (ClassCastException ex){
            Log.e(TAG, "onAttach: " +  ex.getMessage());
        }
    }
}

