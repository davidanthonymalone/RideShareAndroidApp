package inc.david.androidridesharenavigation.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Activities.RegisterActivity;
import inc.david.androidridesharenavigation.Models.Advert;
import inc.david.androidridesharenavigation.R;


public class AllRideShares extends android.app.Fragment {
    private RecyclerView adsList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenter;
    private DatabaseReference mDatabase;
    private DatabaseReference mdatabbaseLike;
    DatabaseReference getmDatabaseUsers;
    private boolean mProcessLike = false;
    private ProgressDialog p;
    private boolean mlike = false;
    private DatabaseReference mDatabaseUsers;

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        mdatabbaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();


        mdatabbaseLike.keepSynced(true);
        mDatabase.keepSynced(true);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_all_ride_shares, container, false);
        adsList = (RecyclerView) v.findViewById(R.id.advertslist);
        adsList.setHasFixedSize(true);
        adsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Advert, AdvertViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Advert, AdvertViewHolder>(
                Advert.class,
                R.layout.advert_row,
                AdvertViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(AdvertViewHolder viewHolder, Advert model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle((model.getTitle()));
                viewHolder.setDesc(model.getDesc());
                viewHolder.setuserName(model.getUsername());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setmLikebtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Fragment fragment;
                        FragmentTransaction fragt = getFragmentManager().beginTransaction();
                        fragt.replace(R.id.homeFrame, new SingolePostFragment()).addToBackStack("").commit();
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
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.drawable.ic_thumb);

                    }else
                        mLikebtn.setImageResource(R.drawable.ic_white);



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