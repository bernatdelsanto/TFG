package com.ub.bernat.listitin.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

public class PlaceList extends ExpandableGroup{
    private String name;
    private String id;
    private ArrayList<Place> places;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public PlaceList(String name, String id, ArrayList<Place> list)

    {
        super(name,list);
        this.name = name;
        this.id = id;
        this.places=list;

    }
    public void addPlace( Place place){
        places.add(place);
    }

    @Override
    public String toString() {
        return "PlaceList{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", places=" + places +
                '}';
    }

    public void remove(int childIndex) {
        if(childIndex<places.size()){
            places.remove(childIndex);
        }
    }
}
