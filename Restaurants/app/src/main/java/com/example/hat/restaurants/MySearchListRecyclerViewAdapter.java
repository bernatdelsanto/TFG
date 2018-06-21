package com.example.hat.restaurants;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hat.restaurants.SearchListFragment.OnListFragmentInteractionListener;
import com.example.hat.restaurants.model.PlaceList;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a PlaceList and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySearchListRecyclerViewAdapter extends RecyclerView.Adapter<MySearchListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<PlaceList> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;

    public MySearchListRecyclerViewAdapter(ArrayList<PlaceList> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_searchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.listName.setText(mValues.get(position).getName());
        holder.listCreator.setText(mValues.get(position).getId());//TODO implement places with creator.

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView listName;
        public final TextView listCreator;
        public PlaceList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            listName = view.findViewById(R.id.listNameView);
            listCreator =  view.findViewById(R.id.listCreatorView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + listCreator.getText() + "'";
        }
    }
}
