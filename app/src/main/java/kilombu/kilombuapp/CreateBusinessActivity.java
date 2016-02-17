package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.core.ServerValues;

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
        getSupportActionBar().setTitle(getString(R.string.title_activity_create_business));

        appRef = new Firebase(getString(R.string.firebase_url));
        businessRef = appRef.child(getString(R.string.child_business));
        detailsRef = appRef.child(getString(R.string.child_business_details));

        categorySelection = (Spinner) findViewById(R.id.form_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapter to the spinner
        categorySelection.setAdapter(adapter);

        stateSelection = (Spinner) findViewById(R.id.form_state);
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
        EditText businessHoursField = (EditText) findViewById(R.id.form_business_hours);
        EditText phoneField = (EditText) findViewById(R.id.form_phone);

        EditText sacPhoneField = (EditText) findViewById(R.id.form_sac_phone);
        EditText emailField = (EditText) findViewById(R.id.form_email);
        EditText websiteField = (EditText) findViewById(R.id.form_website);
        EditText whatsappField = (EditText) findViewById(R.id.form_whatsapp);
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

        String sacPhone = sacPhoneField.getText().toString();
        String email = emailField.getText().toString();
        String website = websiteField.getText().toString();
        String whatsapp = whatsappField.getText().toString();
        String facebook = facebookField.getText().toString();
        String instagram = instagramField.getText().toString();

        //TODO: deal with category as droplist or similar

        String category = categorySelection.getSelectedItem().toString();
        String state = stateSelection.getSelectedItem().toString();

        if (dataIsValid(name, description, corporateNumber,
                    category, state, city, phoneNumber,
                    email, sacPhone, businessHours,
                    whatsapp, facebook, instagram)){
            BusinessAddress address = validateAddress(street, district, city, state);
            Store store = validateStore(address, phoneNumber, businessHours);

            String admin = appRef.getAuth().getUid();
            Business business = new Business(name, admin, category, description, corporateNumber);

            Firebase newBusinessRef = businessRef.push();
            final String businessId = newBusinessRef.getKey();
            newBusinessRef.setValue(business);

            //TODO: check if we need to ask for a unit name
            Map<String, Store> stores = null;
            if (store != null){
                stores = new HashMap<String, Store>();
                stores.put(Integer.toString(storeIndex++) + district, store);
            }
            //TODO: check if there exists already a unit in the district and add an index
            BusinessDetails details = new BusinessDetails(stores, sacPhone, email,
                                    website, whatsapp, facebook, instagram);

            detailsRef.child(businessId).setValue(details);
            detailsRef.child(businessId).setValue(details, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    createBusinessStatistics(businessId);
                    Intent intent = new Intent(CreateBusinessActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void createBusinessStatistics(String businessId){
        Firebase statisticsRef = appRef.child(getString(R.string.child_business_statistics))
                                .child(businessId);
        statisticsRef.setValue(new BusinessStatistics());
        statisticsRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
    }

    //TODO: separate validation methods
    private boolean dataIsValid(String name, String description, String corporateNumber,
                                String category, String state, String city, String phoneNumber,
                                String email, String sacPhone, String businessHours,
                                String whatsapp, String facebook, String instagram){

        boolean isValid = true;

        isValid = isValid && validateName(name)
                && validateDescription(description)
                && validateCorporateNumber(corporateNumber)
                && validateCategory(category)
                && validateState(state, city)
                && validatePhone(phoneNumber)
                && ValidationTools.isValidEmail(email)
                && validatePhone(sacPhone)
                && validatePhone(whatsapp)
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

    private boolean validateCategory(String category){
        if (category.equals(getString(R.string.prompt_category))){
            //TODO: visual feedback
            return false;
        }
        return true;
    }

    private boolean validateState(String state, String city){
        if (state.equals(getString(R.string.prompt_uf))){
            //TODO: visual feedback
            if (! city.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePhone(String phone){
        //TODO: Check how to validate a phone and give visual feedback
        return true;
    }

//    private boolean validateEmail(String email){
//        //Very simple email validation. We should not be very concerned about it, because to provide
//        //a valid email is a concern of the advertiser. So, keeping it simple will do
//        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    private boolean validateFacebookPage(String page){
        //TODO: check how to validate a facebook page
        return true;
    }

    private boolean validateInstagramPage(String page){
        //TODO: check how to validate instagram page
        return true;
    }

    private BusinessAddress validateAddress(String state, String city, String street, String district){
        if (!city.isEmpty() || !street.isEmpty() || !district.isEmpty()){
            return new BusinessAddress(state, city, street, district);
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
