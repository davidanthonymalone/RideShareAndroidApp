package inc.david.androidridesharenavigation.Fragments;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import inc.david.androidridesharenavigation.Fragments.AddProcessFragments.SubAddOne;
import inc.david.androidridesharenavigation.R;

import static android.app.Activity.RESULT_OK;


public class AddFragment extends android.app.Fragment implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        PlaceSelectionListener {

    //all variables required for this class
    View view;
    private ImageButton mSelectImage;
    private Spinner commentText;
    private Spinner goingToText;
    public static Button mSubmitBtn;
    public static double goingToLat;
    public static double goingToLng;
    public static double comingFromLat;
    public static double comingFromLng;
    public static Uri imageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final int REQUEST_SELECT_PLACE = 1000;
    public static String goingTo;
    public static String noOfSeatsSelected;
    public static int noOfSeatsNumber;
    private static final int Gallery_Request = 1;
    public static FirebaseUser mCurrentUser;
    public static DatabaseReference  sortedByCountyDatabase;
    public static StorageReference mStorage;
    public static DatabaseReference rideShareDatabase;
    public static DatabaseReference mDatabaseUsers;
    private static final LatLngBounds WATERFORD_BAR_VIEW = new LatLngBounds(
            new LatLng(52.254539, -7.149922), new LatLng(52.254700, -7.100484));
    private FirebaseAuth auth;
    public static FirebaseUser currentUser;
    public static ProgressDialog mprogress;
    public String title__val, desc_val;
    private OnFragmentInteractionListener mListener;
    Spinner noOfSeatsSpinner;
    public static String comingFrom;
    FrameLayout subAddFrame;
    public static FragmentManager fragmentManager;
    private static final LatLngBounds RideShareBoundary = new LatLngBounds(
            new LatLng(52.324925, -8.466647), new LatLng(52.547628, -6.313327));
    public static String additionalComments;

    public AddFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting title
        getActivity().setTitle("Add Fragment");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getting storage referemnce
        mStorage = FirebaseStorage.getInstance().getReference();
        //getting database reference
        rideShareDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();
        mprogress = new ProgressDialog(getActivity());
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        sortedByCountyDatabase = FirebaseDatabase.getInstance().getReference().child("SortedByCounty");

        view = inflater.inflate(R.layout.fragment_add, container, false);


        //binding data to the weidgets for next few lines

        subAddFrame = (FrameLayout)view.findViewById(R.id.subAddFrame);

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.subAddFrame, new SubAddOne()).commit();
        TextView likedHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        likedHeaderTextView.setText(R.string.AddFragment);

        noOfSeatsSpinner = (Spinner)view.findViewById(R.id.noOfSeatsSpinner);
        ArrayAdapter noOfSeatsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.no_of_seats, android.R.layout.simple_spinner_item);
        noOfSeatsSpinner.setAdapter(noOfSeatsAdapter);

        noOfSeatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noOfSeatsSelected = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        commentText = (Spinner) view.findViewById(R.id.locationComingFrom);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.county_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commentText.setAdapter(adapter);
        commentText.setOnItemSelectedListener(this);


        mSelectImage = (ImageButton) view.findViewById(R.id.imageButton);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);





        return view;

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.locationComingFrom:
                //title__val = parent.getItemAtPosition(position).toString().trim();                break;

            default:
                break;
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //on place selected is the code for the google places.
    //https://developers.google.com/maps/documentation/javascript/examples/places-autocomplete
    //I leanred how to do the below method from the documentation here.
    @Override
    public void onPlaceSelected(Place place) {

        //getting the place selected

        goingTo = place.getAddress().toString();
        goingToLat = place.getLatLng().latitude;
        goingToLng = place.getLatLng().longitude;
        Log.i(LOG_TAG, "WHooo it worked!: " + goingTo);
        Log.v("AddFrag", "goingTo: " + goingTo);
        Log.v("AddFrag", "goingToLat: " + goingToLat);
        Log.v("AddFrag", "goingToLng: " + goingToLng);

        if(!goingTo.isEmpty()) {
            comingFrom = place.getAddress().toString();
        }
         if(goingToLng == 0) {
             comingFromLat = place.getLatLng().latitude;
         }
         if(goingToLat == 0) {
             comingFromLng = place.getLatLng().longitude;
         }
                Log.v("AddFrag", "comingFrom: " + comingFrom);
                Log.v("AddFrag", "comingFromLat: " + comingFromLat);
                Log.v("AddFrag", "comingFromLng: " + comingFromLng);

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            Intent intent = CropImage.activity(imageUri)
                    .getIntent(this.getContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

// for fragment (DO NOT use `getActivity()`)
            // CropImage.activity(imageUri.start(getContext(), this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mSelectImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        //setting the image.
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Request);
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}