package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditStoreInfoActivity extends AppCompatActivity {

    private String businessId;
    private Map<String, Object> storeUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storeUpdates = new HashMap<String, Object>();
        setupStoreInfo();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateStoreInfo();
            }
        });

    }


    private void setupStoreInfo(){
        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");

        String state = intent.getStringExtra(getString(R.string.child_details_store_address_state));
        String city = intent.getStringExtra(getString(R.string.child_details_store_address_city));
        String district = intent.getStringExtra(getString(R.string.child_details_store_address_district));
        String street = intent.getStringExtra(getString(R.string.child_details_store_address_street));
        String phone = intent.getStringExtra(getString(R.string.child_details_store_phone));
        String hours = intent.getStringExtra(getString(R.string.child_details_store_business_hours));

        EditText currentText = (EditText) findViewById(R.id.edit_city);
        currentText.setText(city);

        currentText = (EditText) findViewById(R.id.edit_district);
        currentText.setText(district);

        currentText = (EditText) findViewById(R.id.edit_street);
        currentText.setText(street);

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
        EditText currentText = (EditText) findViewById(R.id.edit_city);
        String city = currentText.getText().toString();

        currentText = (EditText) findViewById(R.id.edit_district);
        String district = currentText.getText().toString();

        currentText = (EditText) findViewById(R.id.edit_street);
        String street = currentText.getText().toString();

        currentText = (EditText) findViewById(R.id.edit_phone);
        String phone = currentText.getText().toString();

        currentText = (EditText) findViewById(R.id.edit_business_hours);
        String hours = currentText.getText().toString();

        Spinner stateSelection = (Spinner) findViewById(R.id.edit_state);
        String state = stateSelection.getSelectedItem().toString();

        BusinessAddress address = ValidationTools.validateAddress(street, district, city, state);
        Store newStore = ValidationTools.validateStore(address, phone, hours);

        Map<String, Object> stores = new HashMap<String, Object>();
        stores.put(Integer.toString(index++) + " " + district, newStore);

        //storeUpdates.put(getString(R.string.child_details_stores), stores);

        Firebase currentDetailsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_details)).child(businessId);

        currentDetailsRef.child(getString(R.string.child_details_stores)).setValue(stores);

        Intent intent = new Intent(this, BusinessProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}
