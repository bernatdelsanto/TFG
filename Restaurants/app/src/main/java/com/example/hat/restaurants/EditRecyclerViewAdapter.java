package com.example.hat.restaurants;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hat.restaurants.controller.DatabaseController;
import com.example.hat.restaurants.model.PlaceList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class EditRecyclerViewAdapter extends RecyclerView.Adapter<EditRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "EditRecyclerViewAdapter";
    private ArrayList<PlaceList> placeLists = new ArrayList<>();
    private Context context;
    private DatabaseReference databaseReference;

    public EditRecyclerViewAdapter(ArrayList<PlaceList> placeLists, Context context) {
        this.placeLists = placeLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_list_parent,parent,false);
        ViewHolder holder = new ViewHolder(view);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = placeLists.get(position).getName();
        holder.listName.setText(name);
        final String placeListID = placeLists.get(position).getId();
        holder.placeListID = placeListID;
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseController controller = new DatabaseController();
                controller.deleteList(placeListID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView deleteView;
        public TextView listName;
        public String placeListID;
        public ViewHolder(View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName);
            deleteView = itemView.findViewById(R.id.deleteListParentView);
        }
    }

}
