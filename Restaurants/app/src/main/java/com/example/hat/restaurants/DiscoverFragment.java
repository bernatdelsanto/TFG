package com.example.hat.restaurants;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment implements OnMapReadyCallback, AddPlaceDialogFragment.OnInputSelected {
    //    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = this.getTag();

    private OnFragmentInteractionListener mListener;
    private Marker focusedMarker;
    private Place focusedPlace;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1111;
    private boolean LOCATION_PERMISSION = false;
    private static final float DEFAULT_MARKER_HUE = BitmapDescriptorFactory.HUE_RED;
    private FusedLocationProviderClient mFusedLocationProviderclient;
    public final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 12312;
    GoogleMap map;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float DEFAULT_BOUND_BIAS_KILOMETERS = 20f;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private GeoDataClient geoDataClient;
    private FloatingActionButton addPlaceFloatingButton;
    private List<Float> colors = Arrays.asList(DEFAULT_MARKER_HUE, BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN);
    private int placeColor = 0;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Discover.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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


        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        geoDataClient = Places.getGeoDataClient(getContext());
        getActivity().setTitle("Discover");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        addPlaceFloatingButton = view.findViewById(R.id.addPlaceButton);
        addPlaceFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusedPlace != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    AddPlaceDialogFragment newFragment = new AddPlaceDialogFragment();
                    newFragment.setTargetFragment(DiscoverFragment.this, 1); //It might generate problems due known Android Bug on fragments inside fragments.
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(R.id.drawer_layout, newFragment).addToBackStack(null).commit();
                    refreshLists();
                }
            }
        });
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        map = googleMap;
        if (LOCATION_PERMISSION) {
            mFusedLocationProviderclient = LocationServices.getFusedLocationProviderClient(getContext());
            map.setMyLocationEnabled(true);
            try {
                Task location = mFusedLocationProviderclient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            }


                        }
                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, "Get device location " + e.getMessage());
            }
        } else {
            LatLng pp = new LatLng(41.386613566833404, 2.1640169620513916);
            addMarker(pp, "Universitat de Barcelona", DEFAULT_MARKER_HUE);
            moveCamera(pp, DEFAULT_ZOOM);
        }

        /*
        *TODO: on click geolocates place
        * map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //save current location
                geoLocateClick(point);
                //remove previously placed Marker

            }
        });;*/
        refreshLists();


    }

    private void moveCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        Log.d(TAG, "MOVING CAMERA TO LAT:" + latLng.latitude + " LONG:" + latLng.longitude + " with ZOOM: " + zoom);
    }

    @Override
    public void sendInput(ArrayList<String> listsIds) {
        if (focusedPlace != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("lists");

            for (String s : listsIds) {
                GeoFire geoFire = new GeoFire(databaseReference.child(s));
                geoFire.setLocation(focusedPlace.getId(), new GeoLocation(focusedPlace.getLatLng().latitude, focusedPlace.getLatLng().longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //TODO: TOAST ERROR
                    }
                });
                databaseReference.child(s).child(focusedPlace.getId()).child("name").setValue(focusedPlace.getName());
            }
            focusedMarker.remove();
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LOCATION_PERMISSION = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }
        LOCATION_PERMISSION = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                final PlaceAutocomplete.IntentBuilder intentBuilder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);


                if (LOCATION_PERMISSION) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        getLocationPermission();
                    }
                    Task location = mFusedLocationProviderclient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();
                            intentBuilder.setFilter(typeFilter);

                            if(task.isSuccessful()){
                                Location currentLocation = (Location) task.getResult();
                                LatLng northEast = new LatLng(currentLocation.getLatitude()+DEFAULT_BOUND_BIAS_KILOMETERS/100,currentLocation.getLongitude()+DEFAULT_BOUND_BIAS_KILOMETERS/100);
                                LatLng southWest = new LatLng(currentLocation.getLatitude()-DEFAULT_BOUND_BIAS_KILOMETERS/100,currentLocation.getLongitude()-DEFAULT_BOUND_BIAS_KILOMETERS/100);

                                intentBuilder.setBoundsBias(new LatLngBounds(southWest,northEast));

                            }
                            DiscoverFragment thisFragment = DiscoverFragment.this;
                            Intent intent = null;
                            try {
                                intent = intentBuilder.build(thisFragment.getActivity());
                                startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }



                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }

        });


        super.onCreateOptionsMenu(menu,inflater);
    }
    private Marker addMarker(LatLng latLng,String title,float hue){
        MarkerOptions option = new MarkerOptions();
        BitmapDescriptor thisIcon = BitmapDescriptorFactory.defaultMarker(hue);
        option.position(latLng).title(title).icon(thisIcon);
        return map.addMarker(option);
    }
    private void geoLogacate(String query) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(query,1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            addMarker(new LatLng(address.getLatitude(),address.getLongitude()),list.get(0).getFeatureName(),DEFAULT_MARKER_HUE);
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM);
        }

    }
    private void geoLocateClick(LatLng latLng){
        List<Address> addresses = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size()>0){
            android.location.Address address = addresses.get(0);
            if (address != null) {
                newFocusedPlace(null,new LatLng(address.getLatitude(),address.getLongitude()));

            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                moveCamera(place.getLatLng(),DEFAULT_ZOOM);
                newFocusedPlace(place,place.getLatLng());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void refreshLists() {
        placeColor =0;
        FirebaseUser user =firebaseAuth.getCurrentUser();
        if ( user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        if (snap!=null){
                            displayList( snap.getKey(), colors.get(placeColor) );
                            placeColor++;
                            placeColor = placeColor%5;

                        }


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
    }

    private void displayList(String key, final float placeColor) {
        databaseReference = FirebaseDatabase.getInstance().getReference("lists").child(key);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild("l")){
                    DataSnapshot location =dataSnapshot.child("l");
                    Double latitudeD= (Double) location.child("0").getValue();
                    Float latitude = latitudeD.floatValue();
                    Double longitudeD= (Double) location.child("1").getValue();
                    Float longitude = longitudeD.floatValue();
                    LatLng latLng = new LatLng(latitude,longitude);
                    addMarker(latLng,dataSnapshot.child("name").getValue(String.class),placeColor);

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

    private void newFocusedPlace(Place place, LatLng latLng){ //TODO: when i have place from latlng, delete parameter
        if (focusedMarker != null) {
            focusedMarker.remove();
        }
        focusedPlace = place;
        focusedMarker = addMarker(latLng,place == null? "":place.getName().toString(),BitmapDescriptorFactory.HUE_YELLOW);
        addPlaceFloatingButton.show();
        int size = addPlaceFloatingButton.getHeight()+addPlaceFloatingButton.getPaddingBottom();
        map.setPadding(0,0,0,size);
    }
    public void removeFocusedPlace(){
        if (focusedMarker!= null){focusedMarker.remove();}
        focusedPlace = null;
        addPlaceFloatingButton.hide();
        map.setPadding(0,0,0,0);
    }

}
