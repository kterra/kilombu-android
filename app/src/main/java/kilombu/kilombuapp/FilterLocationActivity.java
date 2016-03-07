package kilombu.kilombuapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterLocationActivity extends AppCompatActivity {

    private CheckBox chkGps;
    private EditText cityProfile;
    private TextInputLayout inputLayoutCity;
    private android.content.Context context;
    private SharedPreferences userPreferences;
    private boolean isTransition = false;
    private Spinner stateSelection;
    String city, state;
    Float latitude, longitude;
    LocationManager mLocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_filter_location));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = FilterLocationActivity.this;

        chkGps = (CheckBox) findViewById(R.id.chkGps);

        chkGps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        //Do what you need if enabled...
                        try{
                            Location location = getLastKnownLocation();
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
                            SharedPreferences.Editor userEditor = userPreferences.edit();
                            userEditor.putString(getString(R.string.userlat_key), Double.toString(latitude));
                            userEditor.putString(getString(R.string.userlong_key), Double.toString(latitude));
                            Log.d("LAT", Double.toString(latitude));
                            Log.d("LONG", Double.toString(latitude));
                            userEditor.commit();

                            Intent intent = new Intent(FilterLocationActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            isTransition = true;
                            startActivity(intent);
                        }
                        catch (SecurityException e){
                            Toast.makeText(FilterLocationActivity.this,
                                    getString(R.string.toast_forbbiden_action), Toast.LENGTH_LONG).show();
                        }


                    }else{
                        chkGps.setChecked(false);

                        new AlertDialog.Builder(FilterLocationActivity.this)
                                .setIcon(R.drawable.ic_report_problem_black_24dp)
                                .setTitle(getString(R.string.activate_gps_msg))
                                .setMessage(getString(R.string.activage_gps_description_msg))
                                .setPositiveButton(getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                        }

                                })
                                .setNegativeButton(getString(R.string.alert_dialog_negative), null)
                                .show();


                    }
                }

            }
        });





        cityProfile = (EditText) findViewById(R.id.form_city);
        cityProfile.setText(city);

        stateSelection = (Spinner) findViewById(R.id.form_state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSelection.setAdapter(adapterState);



    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        try{
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        }
        catch (SecurityException e){
            Toast.makeText(FilterLocationActivity.this,
                    getString(R.string.toast_forbbiden_action), Toast.LENGTH_LONG).show();
        }
        return bestLocation;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d("MAIN", "ON START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (! isTransition){
            Firebase.goOffline();
            Log.d("MAIN", "GOING OFFLINE");
        }else{
            Log.d("MAIN", "TRANSITION");
        }
        Log.d("MAIN", "ON STOP");
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        isTransition = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void filter(View button){
        city = cityProfile.getText().toString().trim();
        state = stateSelection.getSelectedItem().toString().trim();

        if (chkGps.isChecked()) {
            LocationManager locationManager = (LocationManager) this.getSystemService(context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //Do what you need if enabled...
                try{
                    Location location = getLastKnownLocation();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
                    SharedPreferences.Editor userEditor = userPreferences.edit();
                    userEditor.putString(getString(R.string.userlat_key), Double.toString(latitude));
                    userEditor.putString(getString(R.string.userlong_key), Double.toString(longitude));
                    userEditor.commit();
                    Intent intent = new Intent(FilterLocationActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isTransition = true;
                    startActivity(intent);
                }
                catch (SecurityException e){
                    Toast.makeText(FilterLocationActivity.this,
                           getString(R.string.toast_forbbiden_action), Toast.LENGTH_LONG).show();
                }



            }else{
                //Do what you need if not enabled...
                chkGps.setChecked(false);

                new AlertDialog.Builder(FilterLocationActivity.this)
                        .setIcon(R.drawable.ic_report_problem_black_24dp)
                        .setTitle(getString(R.string.activate_gps_msg))
                        .setMessage(getString(R.string.activage_gps_description_msg))
                        .setPositiveButton(getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton(getString(R.string.alert_dialog_negative), null)
                        .show();
            }

        }else{
            if((!city.isEmpty()) &&(!state.equals("UF"))){

                userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
                SharedPreferences.Editor userEditor = userPreferences.edit();
                String address = city+ ", "+state;
                LatLng latLng = Utils.getLocationFromAddress(context, address);

                userEditor.putString(getString(R.string.userlat_key), Double.toString(latLng.latitude));
                userEditor.putString(getString(R.string.userlong_key), Double.toString(latLng.longitude));
                userEditor.commit();
                Intent intent = new Intent(FilterLocationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                isTransition = true;
                startActivity(intent);

            }else{
                Toast.makeText(FilterLocationActivity.this,
                        getString(R.string.toast_location), Toast.LENGTH_LONG).show();
            }
        }
    }

}
