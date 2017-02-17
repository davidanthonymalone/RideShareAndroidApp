package inc.david.androidridesharenavigation.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingolePostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingolePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingolePostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private DatabaseReference mDatabase;
    private ImageView mBlogSingleImage;
    private TextView mblodtitle, mblogdesc;


    private OnFragmentInteractionListener mListener;

    public SingolePostFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static SingolePostFragment newInstance(String param1, String param2) {
        SingolePostFragment fragment = new SingolePostFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideShare");


        String post_key = MainActivity.tempUid;
        Toast.makeText(getActivity(), post_key, Toast.LENGTH_LONG).show();
        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_imdage = (String) dataSnapshot.child("image").getValue();

                mblodtitle.setText(post_title);
                mblogdesc.setText(post_desc);
                Picasso.with(getActivity()).load(post_imdage).resize(500, 500).into(mBlogSingleImage);

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
        mBlogSingleImage = (ImageView) v.findViewById(R.id.imageView2);
        mblodtitle = (TextView) v.findViewById(R.id.title);
        mblogdesc = (TextView) v.findViewById(R.id.desc);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
