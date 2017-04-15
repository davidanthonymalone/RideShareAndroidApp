package inc.david.androidridesharenavigation.Fragments;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Adapter.CommentList;
import inc.david.androidridesharenavigation.Adapter.CommentsAdapter;
import inc.david.androidridesharenavigation.Fragments.AddProcessFragments.SubAddOne;
import inc.david.androidridesharenavigation.Models.Comment;
import inc.david.androidridesharenavigation.R;

import static inc.david.androidridesharenavigation.Activities.MainActivity.postToBeEdited;
import static inc.david.androidridesharenavigation.Activities.MainActivity.thisPostUID;


public class SinglePostFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static ListView commentList;
    private DatabaseReference mDatabase, mDatabaseUserId;
    private ImageView rideShareSingleImage;
    private TextView comingFromText, desc, goingToText;
    Button commentB;
    public static Button editButton;
    public String post_uid;
    TextView creadBy;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView comments;
    private DatabaseReference mDatabaseComments, mLikesDatabase;
    private DatabaseReference readDatabaseComments, seatsLeftDatabase, mUserDatabase;
    private Button mSingleRemoveButton, loadButton;
    FirebaseAuth mAuth;
    EditText commmentedit;
    GoogleMap mMap;
    View v;
    public double comingFromLat =0, comingFromLng =0, goingToLat =0 , goingToLng =0;
    static String currentUser;
    private TextView additionalcomm;
    public static String createdBy;
    public static String commentCreator;
    public static ArrayList<CommentList> commentsList = new ArrayList<>();
    public static CommentsAdapter commentsAdapter;
    public static TextView remainingSeats;
    DatabaseReference likedByDatabase;
    MapFragment map;
    String post_key = MainActivity.tempUid;
    FragmentManager fragmentManager = getFragmentManager();

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE
    };

    public SinglePostFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static SinglePostFragment newInstance(String param1, String param2) {
        SinglePostFragment fragment = new SinglePostFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        getActivity().setTitle("Single RideShare");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        final String post_key = MainActivity.tempUid;
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("RideShare");
        seatsLeftDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare").child(MainActivity.tempUid).child("seatsRemaining");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        likedByDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare").child(MainActivity.tempUid).child("LikedBy");
        mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser().getUid();
        Query query = mDatabase.child(post_key).child("uid");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    createdBy = dataSnapshot.getValue().toString();
                    if(createdBy.equals(currentUser)){
                        mSingleRemoveButton.setVisibility(View.VISIBLE); //making remove button visible if userID matches the author of ad,
                                                                        //button is now set to GONE by default in layout file
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String post_title = (String) dataSnapshot.child("comingFrom").getValue();
                    String goingTo = (String) dataSnapshot.child("goingTo").getValue();
                    String created = (String) dataSnapshot.child("username").getValue();
                    String addcomments = (String) dataSnapshot.child("additionalComments").getValue();
                    String post_desc = (String) dataSnapshot.child("desc").getValue();
                    String post_imdage = (String) dataSnapshot.child("image").getValue();
                    post_uid = (String) dataSnapshot.child("uid").getValue();

                    comingFromLat = (double) dataSnapshot.child("comingFromLat").getValue();
                    additionalcomm.setText(addcomments);
                    comingFromLng = (double) dataSnapshot.child("comingFromLng").getValue();
                    goingToLat = (double) dataSnapshot.child("goingToLat").getValue();
                    goingToLng = (double) dataSnapshot.child("goingToLng").getValue();

                    comingFromText.setText(post_title);
                    goingToText.setText(goingTo);
                    desc.setText(post_desc);
                    creadBy.setText(created);
                    creadBy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), post_uid, Toast.LENGTH_LONG).show();
                            //MainActivity.tempUid = post_uid;
                            Fragment fragment;
                            FragmentTransaction fragt = getFragmentManager().beginTransaction();
                            fragt.replace(R.id.homeFrame, new UsersProfile()).addToBackStack("").commit();


                        }
                    });

                    Picasso.with(getActivity()).load(post_imdage).resize(500, 500).into(rideShareSingleImage);
                    if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                        mSingleRemoveButton.setVisibility(View.VISIBLE);


                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(v != null){
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try{
            v = inflater.inflate(R.layout.fragment_singole_post, container, false);

        }catch(InflateException e){

        }

        rideShareSingleImage = (ImageView) v.findViewById(R.id.imageView2);
        comingFromText = (TextView) v.findViewById(R.id.title);
        goingToText = (TextView) v.findViewById(R.id.goingToTextview);
        additionalcomm = (TextView) v.findViewById(R.id.additionalcomments) ;
        commentB = (Button) v.findViewById(R.id.commentButton);
        remainingSeats = (TextView)v.findViewById(R.id.remainingSeats);
        editButton = (Button)v.findViewById(R.id.editButton);

        Log.v("SPFragment", "UserID: "+mAuth.getCurrentUser().getUid());
        Log.v("SPFragment", "PostID: "+post_uid);
        showEditButton();

        seatsLeft();

        View mapOverlay = (View) v.findViewById(R.id.mapz);
        mapOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // handle tap
                        break;
                    case MotionEvent.ACTION_UP:
                        // handlerelease
                        break;
                }

                return false; // return false if you want propagate click to map
            }
        });


        creadBy = (TextView) v.findViewById(R.id.createdBy);
        creadBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User_id = post_uid;
                Toast.makeText(getContext(), "me", Toast.LENGTH_LONG);
            }
        });
        commmentedit =(EditText) v.findViewById(R.id.editText) ;

        desc = (TextView) v.findViewById(R.id.comingFrom);
        mSingleRemoveButton = (Button) v.findViewById(R.id.removeButton);


        final String thisPost = MainActivity.tempUid;

        mSingleRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                //When ad is deleted find all users who've liked it and remove from their collections too
                Query usersWhoLiked = mDatabase.child(thisPost).child("LikedBy");
                usersWhoLiked.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot thisUser:dataSnapshot.getChildren()){
                            mUserDatabase.child(thisUser.getValue().toString()).child("LikedRideShares").child(thisPost).removeValue();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //When ad is deleted remove the Likes collection associated with it too
                mLikesDatabase.child(thisPost).removeValue();
                mDatabase.child(thisPost).removeValue();

            }
        });
        commentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commmentedit.getText().toString();
                final  DatabaseReference newPost = mDatabaseComments.child(MainActivity.tempUid).child("Comments").child(mAuth.getCurrentUser().getUid());
                final String advert = MainActivity.tempUid;
                newPost.child("commentText").setValue(comment);
                newPost.child("postedBy").setValue(mAuth.getCurrentUser().getEmail());
                newPost.child("postedByIDText").setValue(mAuth.getCurrentUser().getUid());
                newPost.child("accepted").setValue("No");
                commmentedit.setText("");
                getComments();

                Query usersToUpdate = likedByDatabase;
                usersToUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for(DataSnapshot thisUser:dataSnapshot.getChildren()){
                            final String thisUserTemp = thisUser.getValue().toString();

                            Query getAddToClone = mDatabase.child(advert);
                            getAddToClone.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    mUserDatabase.child(thisUserTemp).child("LikedRideShares").child(MainActivity.tempUid).setValue(dataSnapshot2.getValue());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postToBeEdited = thisPostUID;
                FragmentTransaction fragt = getFragmentManager().beginTransaction();
                fragt.replace(R.id.homeFrame, new AddFragment()).addToBackStack("").commit();

            }
        });

        com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapz);
        mapFragment.getMapAsync(this);
        commentList = (ListView) v.findViewById(R.id.commentRecyclerList);
        Resources resources = getResources();
        commentsAdapter = new CommentsAdapter(getActivity(), commentsList, resources);
        commentList.setAdapter(commentsAdapter);
        getComments();
        //commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //commentList.setHasFixedSize(true);
        return v;
    }

    public void seatsLeft(){
        Query query = seatsLeftDatabase;
        Log.v("SPFragment", "seatsLeftDatabase Query - "+seatsLeftDatabase);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    Log.v("SPFragment", "seatsLeftDatabase Query Snapshot- "+dataSnapshot.getValue());
                    remainingSeats.setText("Seats Remaining: " + dataSnapshot.getValue().toString());

                    int seatsLeft = Integer.parseInt(dataSnapshot.getValue().toString());
                    if(seatsLeft<1){
                        commentB.setVisibility(View.GONE);
                        commmentedit.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void showEditButton(){

        mDatabase.child(thisPostUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    post_uid = (String) dataSnapshot.child("uid").getValue();

                    if(mAuth.getCurrentUser().getUid().equals(post_uid)) {
                        editButton.setVisibility(View.VISIBLE);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public static void getComments(){

        if(!commentsList.isEmpty()){
            commentsList.clear();
        }

        DatabaseReference getTheseComments = FirebaseDatabase.getInstance().getReference().child("RideShare").child(MainActivity.tempUid).child("Comments");
        Query query = getTheseComments;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot thisDataSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = thisDataSnapshot.getValue(Comment.class);
                    CommentList customCommentList = new CommentList();

                    customCommentList.setCommentText(comment.getCommentText());
                    customCommentList.setPostedBy(comment.getPostedby());
                    customCommentList.setPostedByIDText(comment.getPostedByIDText());
                    customCommentList.setAccepted(comment.getAccepted());

                    commentCreator = comment.getPostedByIDText();
                    commentsList.add(customCommentList);
                }

                commentsAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Polyline polyline;
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(comingFromLat, comingFromLng))
                .title("Location Coming From"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(goingToLat, goingToLng))
                .title("Location Going To"));

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng point = new LatLng(52.216367, -7.237313);

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        LatLng s = new LatLng(comingFromLat, comingFromLng);
        LatLng d = new LatLng(goingToLat, goingToLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(s, 3));
        Polyline poly = null;
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(comingFromLat, comingFromLng), new LatLng(goingToLat, goingToLng))
                .width(5)
                .color(Color.RED));


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }



    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

}
