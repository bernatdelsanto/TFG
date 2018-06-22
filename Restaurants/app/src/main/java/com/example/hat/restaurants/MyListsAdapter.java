package com.example.hat.restaurants;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hat.restaurants.controller.DatabaseController;
import com.example.hat.restaurants.model.Place;
import com.example.hat.restaurants.model.PlaceList;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


class ListViewHolder extends GroupViewHolder {

    public ImageView deleteView;
    public TextView listName;
    private ImageView arrow;
    public ImageView followView;
    public ImageView unfollowView;


        public ListViewHolder(View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName);
            deleteView = itemView.findViewById(R.id.deleteListParentView);
            arrow =  itemView.findViewById(R.id.list_item_parent_arrow);
            followView = itemView.findViewById(R.id.followListView);
            unfollowView = itemView.findViewById(R.id.unfollowListView);
        }

        public void setListName(ExpandableGroup group) {
            listName.setText(group.getTitle());
        }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
class PlaceViewHolder extends ChildViewHolder {

    public ImageView deleteView; //TODO: get right
    public TextView placeName;



    public PlaceViewHolder(View itemView) {
        super(itemView);
        placeName = itemView.findViewById(R.id.listName);
        deleteView = itemView.findViewById(R.id.deleteListChildView);
    }


    public void onBind(Place place) {
        placeName.setText(place.getName());
    }

    public void setPlaceName(String placeName) {
        this.placeName.setText(placeName);  ;
    }
}
public class MyListsAdapter extends ExpandableRecyclerViewAdapter<ListViewHolder,PlaceViewHolder> {
    public static final int PERMISSION_EDIT = 0;
    public static final int PERMISSION_FOLLOW = 1;
    public static final int PERMISSION_SEARCH = 2;
    public final DatabaseController databaseController = new DatabaseController();
    public ArrayList<PlaceList> lists;
    public RecyclerView recyclerView;

    private int permission;

    public MyListsAdapter(List<? extends ExpandableGroup> groups, int permission,RecyclerView recyclerView) {
        super(groups);
        lists = (ArrayList<PlaceList>) groups;
        this.permission=permission;
        this.recyclerView = recyclerView;
    }

    @Override
    public ListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_list_parent, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public PlaceViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_list_child, parent, false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final PlaceViewHolder holder, int flatPosition, ExpandableGroup group,
                                      final int childIndex) {
        final Place place = (Place) group.getItems().get(childIndex);
        final PlaceList list = (PlaceList) group;
        if(permission==PERMISSION_EDIT){
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseController.deletePlaceFromList(place.getId(),list.getId());
                    list.remove(childIndex);
                    notifyDataSetChanged();
                }
            });
        }else if(permission==PERMISSION_FOLLOW){
            holder.deleteView.setVisibility(View.GONE);
        }
        holder.setPlaceName(place.getName());
    }

    @Override
    public void onBindGroupViewHolder(ListViewHolder holder, final int flatPosition,
                                      final ExpandableGroup group) {
        final PlaceList placeList = (PlaceList) group;
        holder.setListName(group);
        if(permission==PERMISSION_EDIT){
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.followView.setVisibility(View.GONE);
            holder.unfollowView.setVisibility(View.GONE);
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseController.deleteList(placeList.getId());
                    getGroups().remove(flatPosition);
                    notifyDataSetChanged();
                }
            });
        }else if(permission==PERMISSION_FOLLOW){
            holder.deleteView.setVisibility(View.GONE);
            holder.followView.setVisibility(View.GONE);
            holder.unfollowView.setVisibility(View.VISIBLE);
            holder.unfollowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseController.unfollowList(placeList.getId());
                    getGroups().remove(flatPosition);
                    notifyDataSetChanged();
                }
            });
        }else if (permission==PERMISSION_SEARCH){
            holder.deleteView.setVisibility(View.GONE);
            final ImageView unfollowView = holder.unfollowView;
            unfollowView.setVisibility(View.GONE);
            final ImageView followView = holder.followView;
            followView.setVisibility(View.VISIBLE);
            followView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseController.followList(placeList.getId(),placeList.getName());
                    followView.setVisibility(View.GONE);
                    unfollowView.setVisibility(View.VISIBLE);
                }
            });
            unfollowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseController.unfollowList(placeList.getId());
                    unfollowView.setVisibility(View.GONE);
                    followView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    public List<? extends ExpandableGroup> getList(){
        return super.getGroups();
    }

    public int getPermission(){
        return permission;
    }

   public void deletePlaceFromRecyclerView(RecyclerView recyclerView, int listIndex, int placeIndex){

   }
}
