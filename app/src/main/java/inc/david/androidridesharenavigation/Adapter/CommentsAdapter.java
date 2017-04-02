package inc.david.androidridesharenavigation.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Fragments.AllRideShares;
import inc.david.androidridesharenavigation.Fragments.SinglePostFragment;
import inc.david.androidridesharenavigation.Models.Comment;
import inc.david.androidridesharenavigation.R;

import static inc.david.androidridesharenavigation.Fragments.SinglePostFragment.commentCreator;
import static inc.david.androidridesharenavigation.Fragments.SinglePostFragment.commentList;
import static inc.david.androidridesharenavigation.Fragments.SinglePostFragment.commentsAdapter;


public class CommentsAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    Resources resources;
    CommentList commentList = null;
    Activity activity;
    ArrayList arrayList;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference getmDatabaseShowComments = FirebaseDatabase.getInstance().getReference()
            .child("RideShare").child(MainActivity.tempUid).child("Comments");
    private DatabaseReference usersAcceptedDatabase = FirebaseDatabase.getInstance().getReference()
            .child("RideShare").child(MainActivity.tempUid).child("UsersAccepted");
    private DatabaseReference noOfSeatsDatabase = FirebaseDatabase.getInstance().getReference()
            .child("RideShare").child(MainActivity.tempUid).child("noOfSeats");
    private DatabaseReference seatsRemainingDatabase = FirebaseDatabase.getInstance().getReference()
            .child("RideShare").child(MainActivity.tempUid).child("seatsRemaining");
    String user, adCreator;
    Boolean showAcceptButton = false;
    Boolean showDeleteButton = false;

    long acceptedUsers;
    int remainingSeats;


    public CommentsAdapter(Activity thisActivity, ArrayList thisArrayList, Resources thisResources){

        activity = thisActivity;
        arrayList = thisArrayList;
        resources = thisResources;
        inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private static class ViewHolder{
        TextView postedBy;
        TextView commentText;
        Button acceptButton;
        Button deleteButton;
        TextView postedByIDText;
        TextView acceptedTextView;
        TextView statusTextView;
    }

    @Override
    public int getCount() {
        if(arrayList.size() <= 0){
            return 1;
        }
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if(convertView == null){
            v = inflater.inflate(R.layout.content_comments, null);
            holder = new ViewHolder();
            holder.postedBy = (TextView)v.findViewById(R.id.postedby);
            holder.commentText = (TextView)v.findViewById(R.id.commentText);
            holder.postedByIDText = (TextView)v.findViewById(R.id.postedByIDText);
            holder.acceptButton = (Button)v.findViewById(R.id.acceptButton);
            holder.deleteButton = (Button)v.findViewById(R.id.deleteCommentButton);
            holder.acceptedTextView = (TextView)v.findViewById(R.id.acceptedTextView);
            holder.statusTextView = (TextView)v.findViewById(R.id.statusTextView);
            v.setTag(holder);
        }else holder=(ViewHolder)v.getTag();

        if(arrayList.size()<=0) {
            holder.postedBy.setText("No comments added yet...");
            holder.acceptButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }else{
            commentList = (CommentList) arrayList.get(position);
            holder.postedBy.setText(commentList.getPostedBy());
            holder.commentText.setText(commentList.getCommentText());
            holder.postedByIDText.setText(commentList.getPostedByIDText());
            holder.statusTextView.setText(commentList.getAccepted());



            //Show Delete button if current user is post creator
            if(mAuth.getCurrentUser().getEmail().equals(commentList.getPostedBy())) {
                holder.deleteButton.setVisibility(View.VISIBLE);
            }

            //Show Accept Button if the current user is ad creator, but don't show Accept if
            //they are also Post creator, can't accept their own post
            if(SinglePostFragment.createdBy.equals(mAuth.getCurrentUser().getUid()) &&
                    !mAuth.getCurrentUser().getEmail().equals(commentList.getPostedBy())){
                holder.acceptButton.setVisibility(View.VISIBLE);
            }

            //Replace the comment textview with accepted message, then hide both buttons
            if(commentList.getAccepted().contains("Accepted")){
                holder.acceptedTextView.setVisibility(View.VISIBLE);
                holder.commentText.setVisibility(View.GONE);
                holder.acceptButton.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.GONE);
            }

            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentToAccept = ((CommentList) arrayList.get(position)).postedByIDText;
                    getmDatabaseShowComments.child(commentToAccept).child("accepted").setValue("Accepted");
                    usersAcceptedDatabase.child(((CommentList) arrayList.get(position)).postedByIDText)
                            .setValue(((CommentList) arrayList.get(position)).postedByIDText);

                    Query acceptedUsersNumber = usersAcceptedDatabase;
                    acceptedUsersNumber.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null) {
                                acceptedUsers = dataSnapshot.getChildrenCount();
                            }else {
                                acceptedUsers = 0;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    final Query seatsRemaining = noOfSeatsDatabase;
                    seatsRemaining.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String thisNumber = dataSnapshot.getValue().toString();
                            remainingSeats = Integer.parseInt(thisNumber);
                            int seatsTaken = (int) acceptedUsers;
                            SinglePostFragment.remainingSeats.setText("Seats Remaining:"+(remainingSeats-seatsTaken));
                            seatsRemainingDatabase.setValue(remainingSeats-seatsTaken);

                            SinglePostFragment.getComments();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemToDelete = ((CommentList) arrayList.get(position)).postedByIDText;
                    getmDatabaseShowComments.child(itemToDelete).removeValue();
                    arrayList.remove(position);
                    SinglePostFragment.commentsAdapter.notifyDataSetChanged();
                    SinglePostFragment.commentsAdapter.notifyDataSetInvalidated();
                }
            });
        }

        return v;
    }

}
