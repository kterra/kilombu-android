package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class CreateBusinessActivity extends AppCompatActivity {

    private Firebase appRef;
    private Firebase businessRef;
    private Firebase detailsRef;
    private Spinner categorySelection;
    private int storeIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appRef = new Firebase(getString(R.string.firebase_url));
        businessRef = appRef.child("business");
        detailsRef = appRef.child("BusinessDetails");

        categorySelection = (Spinner) findViewById(R.id.form_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySelection.setAdapter(adapter);
    }

    //Floating send button action
    public void sendForm(View sendButton){
        EditText nameField = (EditText) findViewById(R.id.form_name);
        EditText descriptionField = (EditText) findViewById(R.id.form_description);
        EditText corporateNumberField = (EditText) findViewById(R.id.form_corporate_number);

        EditText cityField = (EditText) findViewById(R.id.form_city);
        EditText streetField = (EditText) findViewById(R.id.form_street);
        EditText districtField = (EditText) findViewById(R.id.form_district);
        EditText phoneField = (EditText) findViewById(R.id.form_phone);

        EditText emailField = (EditText) findViewById(R.id.form_email);
        EditText businessHoursField = (EditText) findViewById(R.id.form_business_hours);
        EditText facebookField = (EditText) findViewById(R.id.form_facebook);
        EditText instagramField = (EditText) findViewById(R.id.form_instagram);

        String name = nameField.getText().toString();
        String description = descriptionField.getText().toString();
        String corporateNumber = corporateNumberField.getText().toString();

        String city = cityField.getText().toString();
        String street = streetField.getText().toString();
        String district = districtField.getText().toString();
        String businessHours = businessHoursField.getText().toString();
        String phoneNumber = phoneField.getText().toString();

        String email = emailField.getText().toString();
        String facebook = facebookField.getText().toString();
        String instagram = instagramField.getText().toString();

        //TODO: deal with category as droplist or similar

        //String category = categorySelection.getSelectedItem().toString();

        String category = "Cultura";
        if (dataIsValid(name, email, city, phoneNumber, description, businessHours, facebook, instagram)){

            BusinessAddress address = new BusinessAddress(city, street, district);
            Store store = new Store(address, phoneNumber, businessHours);

            String admin = appRef.getAuth().getUid();
            Business business = new Business(name, admin, category, description, corporateNumber);


            Firebase newBusinessRef = businessRef.push();
            String businessId = newBusinessRef.getKey();
            newBusinessRef.setValue(business);

            //TODO: check if we need to ask for a unit name
            Map<String, Store> stores = new HashMap<String, Store>();
            //TODO: check if there exists already a unit in the district and add an index
            stores.put(Integer.toString(storeIndex++) + district, store);
            BusinessDetails details = new BusinessDetails(stores, email, facebook,
                    instagram, null, null, null);

            detailsRef.child(businessId).setValue(details);
            /*detailsRef.child(businessId).setValue(details, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Intent intent = new Intent(CreateBusinessActivity.this, BusinessProfileActivity.class);
                    startActivity(intent);
                }
            });*/

        }
        /*Map<String, Store> stores = new HashMap<String, Store>();
        stores.put(district, store);
        BusinessDetails businessDetails = new BusinessDetails();

        detailsRef.child("NINJA").setValue(businessDetails);*/

    }


    //TODO: separate validation methods
    private boolean dataIsValid(String name, String email, String city,
                                String phoneNumber, String description, String businessHours,
                                String facebook, String instagram){

        return true;
    }




}
