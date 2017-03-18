package inc.david.androidridesharenavigation.Fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Models.Comment;
import inc.david.androidridesharenavigation.R;


public class SinglePostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView commentList;
    private DatabaseReference mDatabase, mDatabaseUserId;
    private ImageView rideShareSingleImage;
    private TextView mblodtitle, mblogdesc;
    Button commentB;
    public String post_uid;
    TextView creadBy;
    private RecyclerView comments;
    private DatabaseReference mDatabaseComments, getmDatabaseShowComments;
    private DatabaseReference readDatabaseComments;
    private Button mSingleRemoveButton, loadButton;
    FirebaseAuth mAuth;
    EditText commmentedit;




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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        final String post_key = MainActivity.tempUid;
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("RideShare");


        getmDatabaseShowComments = FirebaseDatabase.getInstance().getReference().child("RideShare").child(MainActivity.tempUid).child("Comments");
        mAuth = FirebaseAuth.getInstance();




        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String created = (String) dataSnapshot.child("username").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_imdage = (String) dataSnapshot.child("image").getValue();
                 post_uid = (String) dataSnapshot.child("uid").getValue();

                mblodtitle.setText(post_title);
                mblogdesc.setText(post_desc);
                creadBy.setText(created);
                creadBy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), post_uid, Toast.LENGTH_LONG).show();
                        MainActivity.tempUid = post_uid;
                        Fragment fragment;
                        FragmentTransaction fragt = getFragmentManager().beginTransaction();
                        fragt.replace(R.id.homeFrame, new UsersProfile()).addToBackStack("").commit();



                    }
                });

                Picasso.with(getActivity()).load(post_imdage).resize(500, 500).into(rideShareSingleImage);
                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mSingleRemoveButton.setVisibility(View.VISIBLE);


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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_singole_post, container, false);
        rideShareSingleImage = (ImageView) v.findViewById(R.id.imageView2);
        mblodtitle = (TextView) v.findViewById(R.id.title);
        commentB = (Button) v.findViewById(R.id.commentButton);



        creadBy = (TextView) v.findViewById(R.id.createdBy);
        creadBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User_id = post_uid;
                Toast.makeText(getContext(), "Rape me", Toast.LENGTH_LONG);
            }
        });
        commmentedit =(EditText) v.findViewById(R.id.editText) ;

        mblogdesc = (TextView) v.findViewById(R.id.leaving);
        mSingleRemoveButton = (Button) v.findViewById(R.id.removeButton);



        mSingleRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(MainActivity.tempUid).removeValue();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        commentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commmentedit.getText().toString();
                final   DatabaseReference newPost = mDatabaseComments.child(MainActivity.tempUid).child("Comments").push();
                String advert = MainActivity.tempUid;
                newPost.child("comment").setValue(comment);
                newPost.child("postedby").setValue(mAuth.getCurrentUser().getEmail().toString());
                commmentedit.setText("");


            }
        });
        commentList = (RecyclerView) v.findViewById(R.id.commentRecyclerList);
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.setHasFixedSize(true);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(

                Comment.class,
                R.layout.content_comments,
                CommentViewHolder.class,
                getmDatabaseShowComments



        ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
                viewHolder.setComment(model.getComment());
                viewHolder.setPostedBy(model.getPostedby());


            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);



    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;



        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setComment(String comment){
            TextView post_comment = (TextView) mView.findViewById(R.id.commentText);
            post_comment.setText(comment);



        }
        public void setPostedBy(String postedby){
            TextView post_postedby = (TextView) mView.findViewById(R.id.postedby);
            post_postedby.setText(postedby);

        }
    }
}
