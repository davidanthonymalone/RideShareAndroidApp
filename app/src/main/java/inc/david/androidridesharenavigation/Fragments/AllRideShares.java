package inc.david.androidridesharenavigation.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Models.Advert;
import inc.david.androidridesharenavigation.R;

import static inc.david.androidridesharenavigation.Activities.MainActivity.thisPostUID;


public class AllRideShares extends android.app.Fragment implements SearchView.OnQueryTextListener {
    public RecyclerView adsList;
    private FirebaseAuth mAuth;
    public FirebaseRecyclerAdapter<Advert, AdvertViewHolder> advertadapter;
    private FirebaseAuth.AuthStateListener mAuthListenter;
    public DatabaseReference rideShareDatabase;
    private DatabaseReference likeDatabase;
    DatabaseReference rideShareDatabaseUsers;
    private DatabaseReference databaseCurrentUser;
    public SearchView searchView;
    private Query mQuery;
    public String d;
    private boolean mProcessLike = false;
    private ProgressDialog p;
    public Query query;
    private boolean mlike = false;
    private DatabaseReference mDatabaseUsers;
    private TextView postedby;
    public   List<String> yourStringArray;
    public static Advert advert;
    ArrayList<Class<Advert>> arrayList = new ArrayList<>();



    View v;


    public AllRideShares() {
        // Required empty public constructor
    }

    public static AllRideShares newInstance() {
        AllRideShares fragment = new AllRideShares();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("All RideShares");
        setHasOptionsMenu(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_ride_shares, container, false);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        rideShareDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        rideShareDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(DataSnapshot dataSnapshot) {


                                                         arrayList = new ArrayList<>();


                                                         // Result will be holded Here
                                                         for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                             arrayList.add(Advert.class); //add result into array list
                                                             String TAG = "";
                                                             Log.d(TAG, "onDataChange: " + arrayList);


                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(DatabaseError databaseError) {

                                                     }


                                                 });
        likeDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        String currentUserId = MainActivity.tempUid;
        databaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("RideShare");

        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);

        postedby = (TextView) v.findViewById(R.id.postedByTextView) ;

        likeDatabase.keepSynced(true);
        rideShareDatabase.keepSynced(true);
        // Inflate the layout for this fragment
        adsList = (RecyclerView) v.findViewById(R.id.advertslist);
        adsList.setHasFixedSize(true);
        adsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        return v;
    }



            // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onStart() {
        super.onStart();


        TextView allHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        allHeaderTextView.setText(R.string.AllRideShares);


        query = rideShareDatabase.orderByChild("timeStamp");
         advertadapter = new FirebaseRecyclerAdapter<Advert, AdvertViewHolder>(
                Advert.class,
                R.layout.advert_row,
                AdvertViewHolder.class,
                query


        ) {
            @Override
            protected void populateViewHolder(AdvertViewHolder viewHolder, final Advert model, int position) {

                advert = model;
                final String post_key = getRef(position).getKey();
                viewHolder.setLeaving((model.getComingFrom()));
                viewHolder.setTitle((model.getGoingTo()));
                viewHolder.setDesc(model.getComingFrom());
                viewHolder.setuserName(model.getUsername());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setmLikebtn(post_key);




                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Fragment fragment;
                        FragmentTransaction fragt = getFragmentManager().beginTransaction();
                        fragt.replace(R.id.homeFrame, new SinglePostFragment()).addToBackStack("").commit();
                        MainActivity.tempUid = post_key;
                        thisPostUID = post_key;
                    }
                });


                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("AllRideShares", "checkpoint1");
                        mProcessLike = true;
                        Log.v("AllRideShares", "checkpoint2");
                        likeDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.v("AllRideShares", "checkpoint3");
                               // GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                Log.v("AllRideShares", "checkpoint4");
                                Log.v("AllRideShares", "dataSnapshot: "+dataSnapshot);
                               // yourStringArray  = dataSnapshot.getValue(t);
                                Log.v("AllRideShares", "checkpoint5");


                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        likeDatabase.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("LikedRideShares").child(post_key).removeValue();
                                        rideShareDatabase.child(post_key).child("LikedBy").child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;



                                    } else {
                                        likeDatabase.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("LikedRideShares").child(post_key).setValue(model);
                                        rideShareDatabase.child(post_key).child("LikedBy").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());

                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


            }
        };


        adsList.setAdapter(advertadapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }




    public interface OnFragmentInteractionListener {
    }


    public static class AdvertViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView post_title, comingFrom;
        ImageButton mLikebtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;



        public AdvertViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mLikebtn = (ImageButton) mView.findViewById(R.id.likeButton);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);



        }



        public void setLeaving(String leaving) {
            TextView leaving_from = (TextView) mView.findViewById(R.id.comingFrom);
            leaving_from.setText(leaving);

        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);

        }

        public void setImage(Context ctx, String image) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

        }



        public void setuserName(String name) {
            TextView post_name = (TextView) mView.findViewById(R.id.postedByTextView);
            post_name.setText(name);
        }
        public void setmLikebtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                            mLikebtn.setImageResource(R.drawable.ic_thumb);

                        } else
                            mLikebtn.setImageResource(R.drawable.ic_white);
                    }catch(Exception exception){

                    }

                    }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFragmentInteraction(Uri uri);
        }

    }




}