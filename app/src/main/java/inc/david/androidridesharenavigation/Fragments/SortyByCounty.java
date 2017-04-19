package inc.david.androidridesharenavigation.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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


public class SortyByCounty extends Fragment implements SearchView.OnQueryTextListener {
    public RecyclerView adsList;
    private FirebaseAuth auth;
    public FirebaseRecyclerAdapter<Advert, AdvertViewHolder> advertadapter;
    private FirebaseAuth.AuthStateListener authListener;
    public DatabaseReference rideShareDatabase;
    private DatabaseReference likeDatabase;
    DatabaseReference usersDatabase;
    private DatabaseReference currentUserDatabaseReference;
    public SearchView searchView;
    private Query mQuery;
    public String d;
    private boolean proccessLike = false;
    private ProgressDialog p;
    public Query query;
    private boolean mlike = false;
    private DatabaseReference mDatabaseUsers;
    private TextView postedby;
    public   List<String> yourStringArray;
    public static Advert advert;
    ArrayList<Class<Advert>> arrayList = new ArrayList<>();
    Spinner pickcounty;
    public String spinnercity;



    View v;


    public SortyByCounty() {
        // Required empty public constructor
    }

    public static SortyByCounty newInstance() {
        SortyByCounty fragment = new SortyByCounty();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Sort By County");
        setHasOptionsMenu(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sortyby_county, container, false);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        rideShareDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        TextView likedHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        likedHeaderTextView.setText(R.string.SortByCounty);

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
        currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("RideShare");


        auth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);

        postedby = (TextView) v.findViewById(R.id.postedByTextView) ;
        pickcounty = (Spinner) v.findViewById(R.id.spinner);


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

        // this is a on item selected listener so when the user picks and item it will requery the database with a new county
        pickcounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    String city = parent.getItemAtPosition(i).toString();
                String TAG = "whoop";
                Log.d(TAG, "onItemSelected: " + city);
                query = rideShareDatabase.orderByChild("city").equalTo(city);
                advertadapter = new FirebaseRecyclerAdapter<Advert, AdvertViewHolder>(
                        Advert.class,
                        R.layout.advert_row,
                        AdvertViewHolder.class,
                        query

                ) {
                    //populate view holder
                    @Override
                    protected void populateViewHolder(AdvertViewHolder viewHolder, final Advert model, int position) {

                        advert = model;
                        final String post_key = getRef(position).getKey();
                        viewHolder.setLeaving((model.getComingFrom()));
                        viewHolder.setTitle((model.getGoingTo()));
                        viewHolder.setDesc(model.getAdditionalComments());
                        viewHolder.setuserName(model.getUsername());
                        viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());




                        //on click listener
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
                                proccessLike = true;

                                likeDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                                        yourStringArray  = dataSnapshot.getValue(t);


                                        if (proccessLike) {
                                            if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                                likeDatabase.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                                mDatabaseUsers.child(auth.getCurrentUser().getUid()).child("LikedRideShares").child(post_key).removeValue();
                                                rideShareDatabase.child(post_key).child("LikedBy").child(auth.getCurrentUser().getUid()).removeValue();
                                                proccessLike = false;



                                            } else {
                                                likeDatabase.child(post_key).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
                                                mDatabaseUsers.child(auth.getCurrentUser().getUid()).child("LikedRideShares").child(post_key).setValue(model);
                                                rideShareDatabase.child(post_key).child("LikedBy").child(auth.getCurrentUser().getUid()).setValue(auth.getCurrentUser().getUid());

                                                proccessLike = false;
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


                //sets adpater for adslist and notifys the database to requery the database.
                adsList.setAdapter(advertadapter);
                advertadapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        TextView allHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        allHeaderTextView.setText(R.string.SortByCounty);

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

    // creates the advert view and displays it

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


        //setting the leaving field

        public void setLeaving(String leaving) {
            TextView leaving_from = (TextView) mView.findViewById(R.id.leavingTime);
            leaving_from.setText(leaving);

        }

        //setting title
        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        //setting description.
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