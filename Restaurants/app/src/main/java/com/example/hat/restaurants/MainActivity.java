package com.example.hat.restaurants;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hat.restaurants.model.PlaceList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Discover.OnFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,MyListsFragment.OnFragmentInteractionListener, SearchListFragment.OnListFragmentInteractionListener,
        FollowingListsFragment.OnFragmentInteractionListener{


private FirebaseAuth firebaseAuth;
private DatabaseReference databaseReference;
private ProgressDialog progressDialog;
private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.collapseActionView();


                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        progressDialog = new ProgressDialog(this);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser()!=null){
            Discover discoverFragment = new Discover();
            setTitle("Discover");
            FragmentManager manager = MainActivity.this.getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,discoverFragment).commit();
            setLogin();

        }else{
            setTitle("Login");
            LoginFragment loginFragment = new LoginFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,loginFragment).commit();
        }


    }

    @Override
    public View onCreateView( String name, Context context, AttributeSet attrs) {

        return super.onCreateView( name, context, attrs);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*Fragment fragmentMain = getSupportFragmentManager().findFragmentById(R.id.mainLayout);
            if (fragmentMain!=null){
                Discover fragmentDiscover = (Discover) fragmentMain.getChildFragmentManager().findFragmentById(R.id.discoverFragment);
                fragmentDiscover.removeFocusedPlace();
            }*/
            //TODO: go to the discover fragment and remove focused place.


            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {

                //super.onBackPressed(); //TODO: implement popbackstack (always returning 0 right now)
            } else {
                getFragmentManager().popBackStack();
            }
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() == null) {
                setTitle("Login");
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout,loginFragment).commit();
            }else{

                setTitle("Profile");
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout,profileFragment).commit();
            }

        } else if (id == R.id.nav_discover) {
            setTitle("Discover");
            Discover discoverFragment = new Discover();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,discoverFragment).commit();

        }else if(id == R.id.nav_mylists){//TODO: only if user logged in
            setTitle("My Lists");
            MyListsFragment myListsFragmentFragment = new MyListsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,myListsFragmentFragment).commit();
        }else if (id == R.id.nav_searchlist){
            setTitle("Search List");
            SearchListFragment searchListFragment = new SearchListFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,searchListFragment).commit();
        }else if (id == R.id.nav_followinglists){
            setTitle("Following Lists");
            FollowingListsFragment followingListsFragment = new FollowingListsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,followingListsFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void login(String email, String password){

        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Successful login",Toast.LENGTH_LONG).show();

                            Discover discoverFragment = new Discover();
                            setTitle("Discover");
                            FragmentManager manager = MainActivity.this.getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.mainLayout,discoverFragment).commit();
                            setLogin();

                        }
                        else{
                            Toast.makeText(MainActivity.this,"User or password not valid",Toast.LENGTH_LONG).show(); //TODO: better explanation
                        }
                        progressDialog.dismiss();
                    }
                });





    }

    public void logout() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        setLogout();
        setTitle("Login");
        LoginFragment loginFragment = new LoginFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout,loginFragment).commit();

    }
    public void setLogout(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem nav_login = menu.findItem(R.id.nav_login);
        nav_login.setTitle("Login");
        View header=navigationView.getHeaderView(0);
        TextView title = (TextView)header.findViewById(R.id.textHeaderTitle);
        TextView subtitle = (TextView)header.findViewById(R.id.textHeaderSubtitle);
        title.setText("");
        subtitle.setText("");
    }
    public void setLogin(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem nav_login = menu.findItem(R.id.nav_login);
        nav_login.setTitle("Profile");
        View header=navigationView.getHeaderView(0);
        final TextView title = header.findViewById(R.id.textHeaderTitle);
        TextView subtitle = header.findViewById(R.id.textHeaderSubtitle);

         databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String user = dataSnapshot.getValue(String.class);
                 title.setText(user);
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
        subtitle.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }




    @Override
    public void onListFragmentInteraction(PlaceList item) {

    }
}
