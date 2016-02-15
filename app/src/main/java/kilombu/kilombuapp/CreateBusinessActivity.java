package kilombu.kilombuapp;

import android.content.Intent;
import android.location.Address;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateBusinessActivity extends AppCompatActivity {

    private Firebase appRef;
    private Firebase businessRef;
    private Firebase detailsRef;
    private Spinner categorySelection;
    private Spinner stateSelection;
    private int storeIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cadastro de Negócio");

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

        stateSelection = (Spinner) findViewById(R.id.state);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSelection.setAdapter(adapterState);
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

        String category = categorySelection.getSelectedItem().toString();

        if (dataIsValid(name, description, corporateNumber, email, phoneNumber, businessHours, facebook, instagram)){
            BusinessAddress address = validateAddress(city, street, district);
            Store store = validateStore(address, phoneNumber, businessHours);

            String admin = appRef.getAuth().getUid();
            Business business = new Business(name, admin, category, description, corporateNumber);

            Firebase newBusinessRef = businessRef.push();
            String businessId = newBusinessRef.getKey();
            newBusinessRef.setValue(business);

            //TODO: check if we need to ask for a unit name
            Map<String, Store> stores = null;
            if (store != null){
                stores = new HashMap<String, Store>();
                stores.put(Integer.toString(storeIndex++) + district, store);
            }
            //TODO: check if there exists already a unit in the district and add an index
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
    }


    //TODO: separate validation methods
    private boolean dataIsValid(String name, String description, String corporateNumber,
                                String email, String phoneNumber, String businessHours,
                                String facebook, String instagram){

        boolean isValid = true;

        isValid = isValid && validateName(name)
                && validateDescription(description)
                && validateCorporateNumber(corporateNumber)
                && validateEmail(email)
                && validatePhone(phoneNumber)
                && validateFacebookPage(facebook)
                && validateInstagramPage(instagram);

        return isValid;
    }

    //Obrigatory
    private boolean validateName(String name){
        if (name.isEmpty()){
            //TODO: Visual Feedback for user
            return false;
        }
        return true;
    }

    private boolean validateDescription(String description){
        if (description.isEmpty()){
            //TODO: Visual Feedback for user
            return false;
        }
        return  true;
    }

    private boolean validateCorporateNumber(String number){
        return true;
    }

    private boolean validatePhone(String phone){
        //TODO: Check how to validate a phone and give visual feedback
        return true;
    }

    private boolean validateEmail(String email){
        //Very simple email validation. We should not be very concerned about it, because to provide
        //a valid email is a concern of the advertiser. So, keeping it simple will do
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validateFacebookPage(String page){
        //TODO: check how to validate a facebook page
        return true;
    }

    private boolean validateInstagramPage(String page){
        //TODO: check how to validate instagram page
        return true;
    }

    private BusinessAddress validateAddress(String city, String street, String district){
        if (!city.isEmpty() || !street.isEmpty() || !district.isEmpty()){
            return new BusinessAddress(city, street, district);
        }
        return null;
    }

    private Store validateStore(BusinessAddress address, String phone, String hours){
        if (address != null || !phone.isEmpty() || !hours.isEmpty()){
            return  new Store(address, phone, hours);
        }
        return null;
    }

}
