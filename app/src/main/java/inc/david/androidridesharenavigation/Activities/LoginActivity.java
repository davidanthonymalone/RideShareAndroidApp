package inc.david.androidridesharenavigation.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import inc.david.androidridesharenavigation.R;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmailField, loginPasswordField;
    private Button loginButton, button;
    private FirebaseAuth auth;
    private DatabaseReference rideShareDatabaseUsers;


    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean flag = true;


    private static final int RC_SIGN_IN = 1;
    private SignInButton mGoogleBtn;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "loginactivity";
    private FirebaseUser currentUSer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //gets the instance of authentication
        auth = FirebaseAuth.getInstance();
        //listens for authenticaion
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null && flag) {

                }


               // flag=false;

            }
        };
        //gets instance of database users
        rideShareDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        //persistence for the database
        rideShareDatabaseUsers.keepSynced(true);
        //binds the data to the widgets
        loginEmailField = (EditText) findViewById(R.id.editTextEmailLogin);
        loginPasswordField = (EditText) findViewById(R.id.editTextPasswordLogin);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleID);
        loginButton = (Button) findViewById(R.id.buttonLogin);

        button = (Button) findViewById(R.id.buttonRegister);

        //listener for google sign in button and launches method when clicked
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInF();

            }
        });




        // =========================Google Sign in  =================================================
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(LoginActivity.this, "Sign in not successful", Toast.LENGTH_LONG).show();

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();

            }
        });
    }


    private void googleSignInF() {

        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }catch(Exception e){
            
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if (result.isSuccess()) {


                try{

                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account);

                Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
                mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                startActivity(mainintent);
                finish();}catch (Exception e){
                    Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                }

            } else {

                // Google Sign In failed, update UI appropriately
                // ...

                Toast.makeText(getApplicationContext(), "set up account before logging in", LENGTH_LONG).show();
            }
        }}catch(Exception c){

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        checkUserExists();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Please register first", LENGTH_LONG).show();
                        }

                        checkUserExists();
                        // ...
                    }
                });
    }



    private void checkLogin() {

        String email = loginEmailField.getText().toString().trim();
        String password = loginPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        try {
                            checkUserExists();
                        }catch (Exception e){

                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Please register first", LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    public void checkUserExists() {
        try{
        if(auth.getCurrentUser() != null){

            final String user_id = auth.getCurrentUser().getUid();
            rideShareDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){
                        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
                       // mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainintent);


                    }else{
                        Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
                      //  setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }}catch(Exception d){

        }}
}
