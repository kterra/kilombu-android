package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditBusinessInfoActivity extends AppCompatActivity {

    private String businessId;
    private Map<String, Object> businessUpdates;
    private TextInputLayout inputLayoutName, inputLayoutDescrption, inputLayoutCorporateNumber;
    private SharedPreferences busPreferences;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        context = EditBusinessInfoActivity.this;
        businessUpdates = new HashMap<String, Object>();
        setupBusinessData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateData();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupBusinessData(){
        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        businessId = busPreferences.getString(getString(R.string.businessid_key), "");
        String businessName = busPreferences.getString(getString(R.string.businessname_key), "");
        String businessDescription = busPreferences.getString(getString(R.string.businessdescription_key), "");
        String businessCategory = busPreferences.getString(getString(R.string.businesscategory_key), "");
        String businessCorporateNumber = busPreferences.getString(getString(R.string.businesscorpnumber_key), "");


        inputLayoutName = (TextInputLayout) findViewById(R.id.edit_name_layout);
        inputLayoutDescrption = (TextInputLayout) findViewById(R.id.edit_description_layout);
        inputLayoutCorporateNumber = (TextInputLayout) findViewById(R.id.edit_corporate_number_layout);

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
        String name = currentText.getText().toString();
        if(!validateName(name)){
            return;
        }
        businessUpdates.put(getString(R.string.child_business_this_name), name);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessname_key), name);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_description);
        String description = currentText.getText().toString();
        if(!validateDescription(description)){
            return;
        }
        businessUpdates.put(getString(R.string.child_business_this_description),
                description);
        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdescription_key), description);
        busEditor.commit();


        currentText = (EditText) findViewById(R.id.edit_corporate_number);
        String corporateNumber = currentText.getText().toString();
        if(!validateCorporateNumber(corporateNumber)){
            return;
        }
        businessUpdates.put(getString(R.string.child_business_this_corporate_number),
                corporateNumber);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businesscorpnumber_key),corporateNumber);
        busEditor.commit();

        Spinner categorySelection = (Spinner) findViewById(R.id.edit_category);
        String category = categorySelection.getSelectedItem().toString();
        if(!validateCategory(category)){
            return;
        }
        businessUpdates.put(getString(R.string.child_business_this_category),
                category);


        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businesscategory_key), category);
        busEditor.commit();

        Firebase currentBusinessRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business)).child(businessId);

        currentBusinessRef.updateChildren(businessUpdates);

        Intent intent = new Intent(getApplicationContext(), BusinessProfileActivity.class);
        //could use noHistory=true on manifest
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //Mandatory
    private boolean validateName(String name){
        if (!ValidationTools.isValidName(name)){
            inputLayoutName.setError(getString(R.string.err_msg_bus_name));
            //requestFocus(nameField);
            Toast.makeText(EditBusinessInfoActivity.this, R.string.err_msg_toast, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    //Mandatory
    private boolean validateDescription(String description){
        if (!ValidationTools.isValidDescription(description)){
            inputLayoutDescrption.setError(getString(R.string.err_msg_descripiton));
            //requestFocus(descriptionField);
            Toast.makeText(EditBusinessInfoActivity.this, R.string.err_msg_toast, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            inputLayoutDescrption.setErrorEnabled(false);
        }
        return  true;
    }

    private boolean validateCorporateNumber(String corporateNumber){
        inputLayoutCorporateNumber.setErrorEnabled(false);
        return true;
    }

    private boolean validateCategory(String category){
        if (category.equals(getString(R.string.prompt_category))){
            Toast.makeText(EditBusinessInfoActivity.this, R.string.err_msg_category, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //TODO: check why it is not working
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
