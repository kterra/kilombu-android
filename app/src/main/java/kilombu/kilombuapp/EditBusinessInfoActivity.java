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
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditBusinessInfoActivity extends AppCompatActivity {

    private String businessId;
    private Map<String, Object> businessUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        businessUpdates = new HashMap<String, Object>();
        setupBusinessData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateData();
            }
        });

    }

    private void setupBusinessData(){
        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");

        String businessName = intent.getStringExtra(
                                getString(R.string.child_business_this_name));
        String businessCategory = intent.getStringExtra(
                                getString(R.string.child_business_this_category));
        String businessDescription = intent.getStringExtra(
                                getString(R.string.child_business_this_description));
        String businessCorporateNumber = intent.getStringExtra(
                                getString(R.string.child_business_this_corporate_number));

        EditText currentText = (EditText) findViewById(R.id.edit_name);
        currentText.setText(businessName);

        currentText = (EditText) findViewById(R.id.edit_description);
        currentText.setText(businessDescription);

        currentText = (EditText) findViewById(R.id.edit_corporate_number);
        currentText.setText(businessCorporateNumber);

        Spinner categorySelection = (Spinner) findViewById(R.id.edit_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        categorySelection.setAdapter(adapter);
        ArrayList<String> categories = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.categories)));
        categorySelection.setSelection(categories.indexOf(businessCategory));
    }

    public void saveAndUpdateData(){
        EditText currentText = (EditText) findViewById(R.id.edit_name);
        businessUpdates.put(getString(R.string.child_business_this_name),
                            currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_description);
        businessUpdates.put(getString(R.string.child_business_this_description),
                            currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_corporate_number);
        businessUpdates.put(getString(R.string.child_business_this_corporate_number),
                            currentText.getText().toString());

        Spinner category = (Spinner) findViewById(R.id.edit_category);
        businessUpdates.put(getString(R.string.child_business_this_category),
                            category.getSelectedItem().toString());

        Firebase currentBusinessRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business)).child(businessId);

        currentBusinessRef.updateChildren(businessUpdates);

        Intent intent = new Intent(getApplicationContext(), BusinessProfileActivity.class);
        //could use noHistory=true on manifest
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
