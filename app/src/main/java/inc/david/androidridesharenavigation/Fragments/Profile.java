package inc.david.androidridesharenavigation.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Activities.SetupActivity;
import inc.david.androidridesharenavigation.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    private ImageButton mSetupImageBtn;
    private EditText mNameField;
    private Button profileButton;
    private static final int GALLERY_REQUEST = 1;
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;
    private StorageReference mStorageImage;
    private OnFragmentInteractionListener mListener;
    private TextView profileName;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mProgress = new ProgressDialog(getActivity());

        TextView profileHeaderTextView = (TextView) getActivity().findViewById(R.id.mainTitle);
        profileHeaderTextView.setText(R.string.Profile);

        mSetupImageBtn = (ImageButton) v.findViewById(R.id.setupImage);
        mNameField = (EditText) v.findViewById(R.id.editTextProfileName);
        profileName = (TextView) v.findViewById(R.id.textProfileName);
        profileButton = (Button) v.findViewById(R.id.profileSetupButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        if(mAuth.getCurrentUser().getPhotoUrl() != null ){
            Picasso.with(getActivity()).load(mAuth.getCurrentUser().getPhotoUrl()).resize(500, 500).into(mSetupImageBtn);

        }
        if(mAuth.getCurrentUser().getDisplayName() != null){
            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            mNameField.setVisibility(View.GONE);

        }

        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {
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
    private void startSetupAccount() {

        final String name = mNameField.getText().toString().trim();

        final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && mImageUri != null){
            mProgress.setMessage("Setting up");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseUsers.child(user_id).child("name").setValue(name);
                    mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                    mProgress.dismiss();
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
// for fragment (DO NOT use `getActivity()`)
            // CropImage.activity(imageUri.start(getContext(), this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImageBtn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
