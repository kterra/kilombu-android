package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kilombu.kilombuapp.models.BusinessAddress;
import kilombu.kilombuapp.models.Store;

public class EditStoreInfoActivity extends AppCompatActivity {

    private final String TAG = "Edit Store";
    private String businessId, state, city, district, street, complement, phone, hours;
    private TextInputLayout inputLayoutCity, inputLayoutDistrict, inputLayoutStreet,
    inputLayoutBusinessHours, inputLayoutPhone;
    private boolean isTransition = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupStoreInfo();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateStoreInfo();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d(TAG, "ON START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (! isTransition){
            Firebase.goOffline();
            Log.d(TAG, "GOING OFFLINE");
        }else{
            Log.d(TAG, "TRANSITION");
        }
        Log.d(TAG, "ON STOP");
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

    private void setupStoreInfo(){
        Intent intent = getIntent();
        String isEmpty =  intent.getStringExtra("empty");

        businessId = intent.getStringExtra("businessId");

        state = intent.getStringExtra(getString(R.string.child_details_store_address_state));
        city = intent.getStringExtra(getString(R.string.child_details_store_address_city));
        district = intent.getStringExtra(getString(R.string.child_details_store_address_district));
        street = intent.getStringExtra(getString(R.string.child_details_store_address_street));
        complement = intent.getStringExtra(getString(R.string.child_details_store_address_complement));
        phone = intent.getStringExtra(getString(R.string.child_details_store_phone));
        hours = intent.getStringExtra(getString(R.string.child_details_store_business_hours));

        EditText currentText = (EditText) findViewById(R.id.edit_city);
        currentText.setText(city);

        currentText = (EditText) findViewById(R.id.edit_district);
        currentText.setText(district);

        currentText = (EditText) findViewById(R.id.edit_street);
        currentText.setText(street);

        currentText = (EditText) findViewById(R.id.edit_complement);
        currentText.setText(complement);

        currentText = (EditText) findViewById(R.id.edit_phone);
        currentText.setText(phone);

        currentText = (EditText) findViewById(R.id.edit_business_hours);
        currentText.setText(hours);

        Spinner stateSelection = (Spinner) findViewById(R.id.edit_state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSelection.setAdapter(adapterState);


        ArrayList<String> states = new ArrayList<String>(Arrays.asList(
                                getResources().getStringArray(R.array.states)));
        stateSelection.setSelection(states.indexOf(state));

    }


    public void saveAndUpdateStoreInfo(){
        int index = 1;

        Spinner stateSelection = (Spinner) findViewById(R.id.edit_state);
        state = stateSelection.getSelectedItem().toString().trim();
        EditText currentText = (EditText) findViewById(R.id.edit_city);
        city = currentText.getText().toString().trim();
        if(!validateState(state, city)){
            return;
        }
        if(!validateCity(city)){
            return;
        }

        currentText = (EditText) findViewById(R.id.edit_district);
        district = currentText.getText().toString().trim();
        if(!validateDistrict(district)){
            return;
        }

        currentText = (EditText) findViewById(R.id.edit_street);
        street = currentText.getText().toString().trim();
        if(!validateStreet(street)){
            return;
        }

        currentText = (EditText) findViewById(R.id.edit_complement);
        complement = currentText.getText().toString().trim();

        currentText = (EditText) findViewById(R.id.edit_phone);
        phone = currentText.getText().toString().trim();
        if(!validatePhone(phone)) {
            return;
        }

        currentText = (EditText) findViewById(R.id.edit_business_hours);
        hours = currentText.getText().toString().trim();
        if(!validateBusinessHours(hours)){
            return;
        }


        BusinessAddress address = ValidationTools.validateAddress(street, complement, district, city, state);
        Store newStore = ValidationTools.validateStore(address, phone, hours);

        Map<String, Object> stores = new HashMap<String, Object>();
        stores.put(Integer.toString(index++) + " " + district, newStore);

        //storeUpdates.put(getString(R.string.child_details_stores), stores);

        Firebase currentDetailsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_details)).child(businessId);

        currentDetailsRef.child(getString(R.string.child_details_stores)).setValue(stores);

        try {
            LatLng latLng = Utils.getLocationFromAddress(this, address.toString());
            SharedPreferences busPreferences = this.getSharedPreferences(getString(R.string.preference_business_key),
                    android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor busEditor = busPreferences.edit();
            String category = busPreferences.getString(getString(R.string.businesscategory_key), "");
            busEditor.putString(getString(R.string.businesscategory_key), category);
            busEditor.commit();
            //set on selected category
            GeoFire geoFire = new GeoFire(new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business_geolocation)).child(category));
            geoFire.setLocation(businessId, new GeoLocation(latLng.latitude, latLng.longitude));
            //set on category "todas"
            geoFire = new GeoFire(new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business_geolocation)).child(getString(R.string.category_all)));
            geoFire.setLocation(businessId, new GeoLocation(latLng.latitude, latLng.longitude));
        } catch (NullPointerException e){
            Toast.makeText(this, R.string.toast_address_not_found, Toast.LENGTH_LONG);
        }

        Intent intent = new Intent(this, BusinessProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        isTransition = true;
        startActivity(intent);

    }

    private boolean validateState(String state, String city){
        if (state.equals(getString(R.string.prompt_uf)) &&  ! city.isEmpty()){
            Toast.makeText(EditStoreInfoActivity.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateCity(String city){
        inputLayoutCity = (TextInputLayout) findViewById(R.id.edit_city_layout);
        inputLayoutCity.setErrorEnabled(false);
        return true;
    }

    private boolean validateDistrict(String district){
        inputLayoutDistrict = (TextInputLayout) findViewById(R.id.edit_district_layout);
        inputLayoutDistrict.setErrorEnabled(false);
        return true;
    }

    private boolean validateStreet(String street){
        inputLayoutStreet = (TextInputLayout) findViewById(R.id.edit_street_layout);
        inputLayoutStreet.setErrorEnabled(false);
        return true;
    }

    private boolean validateBusinessHours(String businessHours){
        inputLayoutBusinessHours = (TextInputLayout) findViewById(R.id.edit_business_hours_layout);
        inputLayoutBusinessHours.setErrorEnabled(false);
        return true;
    }

    private boolean validatePhone(String phone){
        inputLayoutPhone  = (TextInputLayout) findViewById(R.id.edit_phone_layout);
        inputLayoutPhone.setErrorEnabled(false);
        return true;
    }

}
