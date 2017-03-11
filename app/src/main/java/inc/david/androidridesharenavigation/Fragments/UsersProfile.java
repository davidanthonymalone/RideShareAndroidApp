package inc.david.androidridesharenavigation.Fragments;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Models.Comment;
import inc.david.androidridesharenavigation.Models.UserFeedback;
import inc.david.androidridesharenavigation.R;

import static android.app.Activity.RESULT_OK;


public class UsersProfile
        extends Fragment {

    private RecyclerView commentList;
    private ImageButton mSetupImageBtn;
    private TextView mNameField;
    private Button profileButton;
    private static final int GALLERY_REQUEST = 1;
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    Button button;
    private Double stars;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;
    private EditText feedback;
    private StorageReference mStorageImage;
    private AllRideShares.AdvertViewHolder.OnFragmentInteractionListener mListener;
    private TextView profileName;
    private DatabaseReference getmDatabaseShowComments;
    public String photoUrl;
    private RatingBar ratings, avgRating;
    View v;
    FirebaseUser user;

    public UsersProfile() {
        // Required empty public constructor
    }

    public static UsersProfile newInstance() {
        UsersProfile fragment = new UsersProfile();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getmDatabaseShowComments = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.tempUid).child("Feedback");
        

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_users_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mProgress = new ProgressDialog(getActivity());
        ratings = (RatingBar) v.findViewById(R.id.ratingBar1);

        commentList = (RecyclerView) v.findViewById(R.id.userRecycler);
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.setHasFixedSize(true);
        feedback = (EditText) v.findViewById(R.id.feedbackEdit);

        avgRating = (RatingBar) v.findViewById(R.id.avgRating);





        mSetupImageBtn = (ImageButton) v.findViewById(R.id.setupImage);
        profileName = (TextView) v.findViewById(R.id.editTextProfileName);
        button = (Button) v.findViewById(R.id.feedbackButton);
        profileButton = (Button) v.findViewById(R.id.profileSetupButton);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.tempUid);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = feedback.getText().toString();
                String rating = String.valueOf(ratings.getRating());
                stars = Double.parseDouble(rating);
                Toast.makeText(getActivity(), rating, Toast.LENGTH_LONG).show();
                final DatabaseReference newPost = mDatabaseUsers.child("Feedback").push();
                String advert = MainActivity.tempUid;
                String name = mAuth.getCurrentUser().getEmail();
                newPost.child("comment").setValue(comment);
                newPost.child("postedby").setValue(name);
                newPost.child("rating").setValue(stars);

                feedback.setText("");

            }
        });
        mDatabaseUsers.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                profileName.setText(s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photoUrl = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (mDatabaseUsers != null) {

        }



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Fragment fragment;
        FragmentTransaction fragt = getFragmentManager().beginTransaction();
        fragt.replace(R.id.homeFrame, new AllRideShares()).addToBackStack("").commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabaseUsers.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                profileName.setText(s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<UserFeedback, UsersProfile.UserProfileFeedbac> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserFeedback, UserProfileFeedbac>(

                UserFeedback.class,
                R.layout.user_content_comments,
                UsersProfile.UserProfileFeedbac.class,
                getmDatabaseShowComments


        ) {


            @Override
            protected void populateViewHolder(UserProfileFeedbac viewHolder, UserFeedback model, int position) {
                viewHolder.setComment(model.getComment());
                viewHolder.setPostedBy(model.getPostedby());
                viewHolder.setRatings(model.getRating());

            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);


    }


    public static class UserProfileFeedbac extends RecyclerView.ViewHolder {
        View mView;


        public UserProfileFeedbac(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment(String comment) {
            TextView post_comment = (TextView) mView.findViewById(R.id.commentText);

            post_comment.setText(comment);


        }

        public void setPostedBy(String postedby) {
            TextView post_postedby = (TextView) mView.findViewById(R.id.postedby);
            post_postedby.setText(postedby);

        }

        public void setRatings(Double rating) {

            String total2 = String.valueOf(rating);

            RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar1);
            ratingBar.setEnabled(false);
            ratingBar.setMax(5);
            ratingBar.setStepSize(0.01f);
            ratingBar.setRating(Float.parseFloat(total2));
            ratingBar.invalidate();
        }

    }

}