package com.ub.bernat.listitin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ub.bernat.listitin.model.PlaceList;

import java.util.ArrayList;

/*
*
* Regular flat RecyclerViewAdapter, only used by AddPlaceDialog to display which lists to add the place in.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<PlaceList> places= new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(ArrayList<PlaceList> places, Context context) {
        this.places = places;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_check_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = places.get(position).getName();
            holder.checkbox.setText(name);
            holder.placeID = places.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return places.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public CheckBox checkbox;
        public String placeID;
        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkBox);
        }
    }
}
