package inc.david.androidridesharenavigation.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;




public class Base extends AppCompatActivity {

//base class which inherits which the main activity inherits from.  I built the app this way with an intention to use this class but i never really got around to using it.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locMan=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);



    }

    protected void goToActivity(Activity current,
                                Class<? extends Activity> activityClass,
                                Bundle bundle) {
        Intent newActivity = new Intent(current, activityClass);

        if (bundle != null) newActivity.putExtras(bundle);

        current.startActivity(newActivity);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //

        return true;
    }}




