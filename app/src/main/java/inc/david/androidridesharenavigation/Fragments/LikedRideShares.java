package inc.david.androidridesharenavigation.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Models.Advert;
import inc.david.androidridesharenavigation.R;


public class LikedRideShares extends android.app.Fragment {
    private RecyclerView adsList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenter;
    private DatabaseReference mDatabase;
    private DatabaseReference mdatabbaseLike;
    DatabaseReference getmDatabaseUsers;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQuery;
    private boolean mProcessLike = false;
    private ProgressDialog p;
    private boolean mlike = false;
    private DatabaseReference mDatabaseUsers;
    private TextView postedby;

    View v;


    public LikedRideShares() {
        // Required empty public constructor
    }

    public static LikedRideShares newInstance() {
        LikedRideShares fragment = new LikedRideShares();
        return fragment;
    }


    //the on create method and just sets the title to the name of the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Liked RideShares");

    }

    //same as any other fragment you just bind the widgets to the code here and declare istances of database/auth etc
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_ride_shares, container, false);

        //todo:Changing the heading from ALL to LIKED RIDESHARES
        TextView likedHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        likedHeaderTextView.setText(R.string.LikedRideShares);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        mdatabbaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        String currentUserId = MainActivity.tempUid;
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("RideShare");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        postedby = (TextView) v.findViewById(R.id.postedByTextView) ;

        mdatabbaseLike.keepSynced(true);
        mDatabase.keepSynced(true);
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


        Query query = mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("LikedRideShares");
        Log.v("LikedRideShare","Query: "+query.toString());

        //Frebase recycler adapter is a useful way ot laying out your list using a recycler adapter.
        FirebaseRecyclerAdapter<Advert, AdvertViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Advert, AdvertViewHolder>(
                Advert.class,
                R.layout.advert_row,
                AdvertViewHolder.class,
                query



        ){
            @Override
            protected void populateViewHolder(AdvertViewHolder viewHolder, Advert model, int position) {



                //setting and populting the viewholder
                Advert advert = model;
                final String post_key = getRef(position).getKey();
                viewHolder.setLeaving((model.getComingFrom()));
                viewHolder.setTitle((model.getGoingTo()));
                viewHolder.setDesc(model.getAdditionalComments());
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


                    }
                });
                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;

                        mdatabbaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mdatabbaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("LikedRideShares").child(post_key).removeValue();
                                        mDatabase.child(post_key).child("LikedBy").child(mAuth.getCurrentUser().getUid()).removeValue();
                                        notifyDataSetChanged();
                                        mProcessLike = false;

                                    } else {
                                        mdatabbaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
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

        adsList.setAdapter(firebaseRecyclerAdapter);

    }

    public interface OnFragmentInteractionListener {
    }


    public static class AdvertViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView post_title;
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

        //just methodds below to set all the varabiesl to the variables.
        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
            Log.v("LikedRideShare","post_title.setText: "+title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setLeaving(String leaving) {
            TextView leaving_from = (TextView) mView.findViewById(R.id.leavingTime);
            leaving_from.setText(leaving);

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