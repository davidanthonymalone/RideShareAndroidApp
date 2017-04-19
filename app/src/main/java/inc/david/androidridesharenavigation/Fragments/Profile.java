package inc.david.androidridesharenavigation.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.R;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment {

    private ImageButton setupImgBtn;
    private EditText nameField;
    private Button profileButton;
    private static final int GALLERY_REQUEST = 1;
    private FirebaseAuth auth;
    private Uri mImageUri = null;
    private DatabaseReference databaseUsers;
    private ProgressDialog progress;
    private StorageReference storageImg;
    private OnFragmentInteractionListener mListener;
    private TextView profileName, emailtext;
    View v;

    public Profile() {
        // Required empty public constructor
    }
    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Profile");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        storageImg = FirebaseStorage.getInstance().getReference().child("Profile_images");
        progress = new ProgressDialog(getActivity());

        TextView profileHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        profileHeaderTextView.setText(R.string.Profile);


        //binding the variables to the widgets.
        setupImgBtn = (ImageButton) v.findViewById(R.id.setupImage);
        nameField = (EditText) v.findViewById(R.id.editTextProfileName);
        profileName = (TextView) v.findViewById(R.id.textProfileName);
        emailtext = (TextView) v.findViewById(R.id.emailtext);
        profileButton = (Button) v.findViewById(R.id.profileSetupButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        if(auth.getCurrentUser().getPhotoUrl() != null ){
            Picasso.with(getActivity()).load(auth.getCurrentUser().getPhotoUrl()).resize(500, 500).into(setupImgBtn);

        }
        if(auth.getCurrentUser().getDisplayName() != null){
            profileName.setText(auth.getCurrentUser().getDisplayName());
            emailtext.setText(auth.getCurrentUser().getEmail());
            nameField.setVisibility(View.GONE);


        }

        //just a onclick listener for the image
        setupImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //does the setting up of the acccount.
    private void startSetupAccount() {

        final String name = nameField.getText().toString().trim();

        final String user_id = auth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && mImageUri != null){
            progress.setMessage("Setting up");
            progress.show();

            StorageReference filepath = storageImg.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    databaseUsers.child(user_id).child("name").setValue(name);
                    databaseUsers.child(user_id).child("image").setValue(downloadUri);
                    progress.dismiss();
                    Intent setupIntent = new Intent(getActivity(), MainActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);


                }
            });




        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            Intent intent = CropImage.activity(imageUri)
                    .getIntent(getContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                setupImgBtn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
