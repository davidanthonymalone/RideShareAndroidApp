package inc.david.androidridesharenavigation.Fragments.AddProcessFragments;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import inc.david.androidridesharenavigation.Activities.MainActivity;
import inc.david.androidridesharenavigation.Fragments.AddFragment;
import inc.david.androidridesharenavigation.R;

import static inc.david.androidridesharenavigation.Activities.MainActivity.postToBeEdited;
import static inc.david.androidridesharenavigation.R.id.auto;
import static inc.david.androidridesharenavigation.R.id.subAddFrame;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubAddOne extends Fragment implements View.OnClickListener, PlaceSelectionListener, AdapterView.OnItemSelectedListener {


    Button startButton, nextButton;
    public static TextView instructionsTV, fromWhereTV, noOfSeatsTV;
    Spinner noOfSeatsSpinner;
    EditText finaldetails;
    TableRow searchRow;
    PlaceAutocompleteFragment autocompleteFragment;
    private static final LatLngBounds RideShareBoundary = new LatLngBounds(
            new LatLng(52.324925, -8.466647), new LatLng(52.547628, -6.313327));
    
    public SubAddOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_add_one, container, false);
        
        startButton = (Button)view.findViewById(R.id.startButton);
        nextButton = (Button)view.findViewById(R.id.nextButton);


        instructionsTV = (TextView)view.findViewById(R.id.instructionsTV2);
        noOfSeatsTV = (TextView)view.findViewById(R.id.noSeatsTV);
        fromWhereTV = (TextView)view.findViewById(R.id.fromWhereTV);

        noOfSeatsSpinner = (Spinner)view.findViewById(R.id.noOfSeatsSpinner);
        ArrayAdapter noOfSeatsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.no_of_seats, android.R.layout.simple_spinner_item);
        noOfSeatsSpinner.setAdapter(noOfSeatsAdapter);

        searchRow = (TableRow)view.findViewById(R.id.searchRow);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_fragment);

        noOfSeatsSpinner.setOnItemSelectedListener(this);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setBoundsBias(RideShareBoundary);
        startButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        if(!postToBeEdited.equals("")){
            TextView instructionsTV2 = (TextView)view.findViewById(R.id.instructionsTV2);
            instructionsTV2.setText("Start editing your Rideshare ad by clicking start.");
        }
                
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.startButton:

                startButton.setVisibility(View.GONE);
                instructionsTV.setText(R.string.instructions_add_leave);

                noOfSeatsTV.setVisibility(View.VISIBLE);
                noOfSeatsSpinner.setVisibility(View.VISIBLE);
                fromWhereTV.setVisibility(View.VISIBLE);
                searchRow.setVisibility(View.VISIBLE);

                break;

            case R.id.nextButton:

                if(noOfSeatsSpinner.getSelectedItem()==null || AddFragment.comingFrom==null){
                    Toast.makeText(getActivity(), "Something's wrong, recheck seats and address.",Toast.LENGTH_SHORT).show();
                }else {
                    AddFragment.fragmentManager.beginTransaction().replace(subAddFrame, new SubAddTwo()).commit();
                }

                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {

        AddFragment.comingFrom = place.getAddress().toString();
        AddFragment.comingFromLat = place.getLatLng().latitude;
        AddFragment.comingFromLng = place.getLatLng().longitude;

        if(AddFragment.comingFrom != null && AddFragment.noOfSeatsSelected != null){
            Log.v("SubAddOne","noOfSeatsSelected - "+AddFragment.noOfSeatsSelected);
            Log.v("SubAddOne","comingFrom - "+AddFragment.comingFrom);
            nextButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AddFragment.noOfSeatsSelected = parent.getSelectedItem().toString();
        Log.v("SubAddOne","noOfSeatsSelected - "+AddFragment.noOfSeatsSelected);
        Log.v("SubAddOne","comingFrom - "+AddFragment.comingFrom);

        if(AddFragment.comingFrom != null && AddFragment.noOfSeatsSelected != null){
            Log.v("SubAddOne","noOfSeatsSelected - "+AddFragment.noOfSeatsSelected);
            Log.v("SubAddOne","comingFrom - "+AddFragment.comingFrom);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
