package inc.david.androidridesharenavigation.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import inc.david.androidridesharenavigation.Fragments.AddFragment;
import inc.david.androidridesharenavigation.Fragments.AllRideShares;
import inc.david.androidridesharenavigation.Fragments.LikedRideShares;
import inc.david.androidridesharenavigation.Fragments.MapsActivity2;
import inc.david.androidridesharenavigation.Fragments.Profile;
import inc.david.androidridesharenavigation.R;


public class MainActivity extends Base
        implements NavigationView.OnNavigationItemSelectedListener, AllRideShares.OnFragmentInteractionListener {
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUsers;
    FirebaseAuth.AuthStateListener mAuthListenter;
    FirebaseUser currentUSer;

    public static String tempUid = null;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mAuth = FirebaseAuth.getInstance();
        currentUSer = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        mDatabase.keepSynced(true);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mAuthListenter = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        TextView name = (TextView)header.findViewById(R.id.nav_name);
       TextView  email = (TextView)header.findViewById(R.id.personsemail);
        ImageView image = (ImageView)header.findViewById(R.id.imageView);
        if(currentUSer != null) {
            name.setText(currentUSer.getDisplayName());
            email.setText(currentUSer.getEmail());
            Picasso.with(this).load(currentUSer.getPhotoUrl()).into(image);
        }




        navigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        AllRideShares fragment = AllRideShares.newInstance();
        ft.replace(R.id.homeFrame, fragment);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExists();

        mAuth.addAuthStateListener(mAuthListenter);

    }
    private void checkUserExists() {


        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction fragt = getFragmentManager().beginTransaction();

        if (id == R.id.nav_home) {
            fragment = AllRideShares.newInstance();
            fragt.replace(R.id.homeFrame, fragment);
            fragt.addToBackStack(null);
            fragt.commit();

        } else if(id == R.id.nav_liked){
            fragt.replace(R.id.homeFrame, new LikedRideShares()).addToBackStack("").commit();

        }

        else if (id == R.id.nav_add) {
            fragt.replace(R.id.homeFrame, new AddFragment()).addToBackStack("").commit();


        } else if (id == R.id.nav_map) {
            fragt.replace(R.id.homeFrame, new Profile()).addToBackStack("").commit();



        }else if (id == R.id.nav_share){
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();

    }



}
