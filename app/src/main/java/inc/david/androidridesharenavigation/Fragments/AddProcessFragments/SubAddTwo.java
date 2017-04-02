package inc.david.androidridesharenavigation.Fragments.AddProcessFragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Fragments.AddFragment;
import inc.david.androidridesharenavigation.R;

import static inc.david.androidridesharenavigation.Fragments.AddFragment.comingFrom;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.comingFromLat;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.comingFromLng;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.goingTo;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.goingToLat;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.goingToLng;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.mCurrentUser;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.mDatabase;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.mDatabaseUsers;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.mImageUri;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.mprogress;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.noOfSeatsNumber;
import static inc.david.androidridesharenavigation.Fragments.AddFragment.noOfSeatsSelected;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubAddTwo extends Fragment implements View.OnClickListener, PlaceSelectionListener {

    RelativeLayout addDestinationRelLayout, reviewRelLayout;
    Button finalNextButton, mSubmitBtn;
    TextView instructionsTV2, comingFromTV, goingToTV, noOfSeatsTV;
    PlaceAutocompleteFragment autocompleteFragment;

    public SubAddTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_add_two, container, false);

        addDestinationRelLayout = (RelativeLayout)view.findViewById(R.id.addDestinationRelLayout);
        reviewRelLayout = (RelativeLayout)view.findViewById(R.id.reviewRelLayout);
        instructionsTV2 = (TextView)view.findViewById(R.id.instructionsTV2);
        comingFromTV = (TextView)view.findViewById(R.id.comingFromTV);
        goingToTV = (TextView)view.findViewById(R.id.goingToTVSummary);
        noOfSeatsTV = (TextView)view.findViewById(R.id.noOfSeatsTVSummary);

        finalNextButton = (Button)view.findViewById(R.id.finalNextButton);
        mSubmitBtn = (Button)view.findViewById(R.id.submitButton);
        mSubmitBtn.setOnClickListener(this);
        finalNextButton.setOnClickListener(this);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_fragment_2);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.finalNextButton:
                instructionsTV2.setText(R.string.instructions_review);
                addDestinationRelLayout.setVisibility(View.GONE);
                reviewRelLayout.setVisibility(View.VISIBLE);

                comingFromTV.setText(comingFrom);
                goingToTV.setText(goingTo);
                noOfSeatsTV.setText(noOfSeatsSelected);


                break;

            case R.id.submitButton:

                if(noOfSeatsSelected != null) {
                    noOfSeatsNumber = Integer.parseInt(noOfSeatsSelected);
                }
                // final String desc_val = goingToText.getText().toString().trim();
                if(!TextUtils.isEmpty(comingFrom)  && mImageUri != null){
                    mprogress.show();

                    StorageReference filepath = AddFragment.mStorage.child("RideShare").child(mImageUri.getLastPathSegment());
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newPost = mDatabase.push();




                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("noOfSeats").setValue(noOfSeatsNumber);
                                    newPost.child("seatsRemaining").setValue(noOfSeatsNumber);

                                    newPost.child("goingTo").setValue(goingTo);
                                    newPost.child("goingToLat").setValue(goingToLat);
                                    newPost.child("goingToLng").setValue(goingToLng);

                                    newPost.child("comingFrom").setValue(comingFrom);
                                    newPost.child("comingFromLat").setValue(comingFromLat);
                                    newPost.child("comingFromLng").setValue(comingFromLng);

                                    newPost.child("image").setValue(downloadUrl.toString());
                                    newPost.child("uid").setValue(mCurrentUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mprogress.dismiss();
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                getActivity().startActivity(intent);


                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }

                break;

        }

    }

    @Override
    public void onPlaceSelected(Place place) {
        goingTo = place.getAddress().toString();
        goingToLat = place.getLatLng().latitude;
        goingToLng = place.getLatLng().longitude;

        Log.v("SubAddTwo","goingTo - "+ goingTo);
        Log.v("SubAddTwo","goingToLat - "+ goingToLat);
        Log.v("SubAddTwo","goingToLng - "+ goingToLng);

        if(goingTo != null){
            finalNextButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onError(Status status) {

    }
}
