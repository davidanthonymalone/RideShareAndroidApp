package inc.david.androidridesharenavigation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import inc.david.androidridesharenavigation.R;

public class SetupActivity extends AppCompatActivity {

    //declare all variables.
    private ImageButton setUpImageBttn;
    private EditText nameField;
    private Button profileButton;
    private static final int GALLERY_REQUEST = 1;
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    private DatabaseReference rideShareDatabaseUsers;
    private ProgressDialog mProgress;
    private StorageReference storageImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth = FirebaseAuth.getInstance();
        storageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mProgress = new ProgressDialog(this);

        //binds the widgets to the views.

        setUpImageBttn = (ImageButton) findViewById(R.id.setupImage);
        nameField = (EditText) findViewById(R.id.editTextProfileName);
        profileButton = (Button) findViewById(R.id.profileSetupButton);
        rideShareDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });

        //sets up the image button
        setUpImageBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

    }


    //method to start up the registration of the account.
    private void startSetupAccount() {

        final String name = nameField.getText().toString().trim();

       final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && mImageUri != null){
            mProgress.setMessage("Setting up");
            mProgress.show();

            //the image is stored in the storage and a reference to it is saved in the realtime database.  When I use an image i put the uri into the imageview and that displays the image.

            StorageReference filepath = storageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                     rideShareDatabaseUsers.child(user_id).child("name").setValue(name);
                    rideShareDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                    mProgress.dismiss();
                    Intent setupIntent = new Intent(SetupActivity.this, MainActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);


                }
            });




        }
    }

    //http://square.github.io/picasso/ the code for picasso was got here.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            Intent intent = CropImage.activity(imageUri)
                    .getIntent(this.getApplicationContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

// for fragment (DO NOT use `getActivity()`)
           // CropImage.activity(imageUri.start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                setUpImageBttn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
