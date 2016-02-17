package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
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
    private EditText nameField, descriptionField, corporateNumberField, cityField, streetField,
            districtField, businessHoursField,phoneField, sacPhoneField, emailField, websiteField,
            whatsappField, facebookField, instagramField;
    private TextInputLayout inputLayoutName, inputLayoutDescrption, inputLayoutCorporateNumber,
            inputLayoutCity, inputLayoutDistrict, inputLayoutStreet,  inputLayoutBusinessHours, inputLayoutPhone,
            inputLayoutSacPhone, inputLayoutEmail, inputLayoutWebsite, inputLayoutWhatsapp,
            inputLayoutFacebook, inputLayoutInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_create_business));

        nameField = (EditText) findViewById(R.id.form_name);
        descriptionField = (EditText) findViewById(R.id.form_description);
        corporateNumberField = (EditText) findViewById(R.id.form_corporate_number);

        cityField = (EditText) findViewById(R.id.form_city);
        streetField = (EditText) findViewById(R.id.form_street);
        districtField = (EditText) findViewById(R.id.form_district);
        businessHoursField = (EditText) findViewById(R.id.form_business_hours);
        phoneField = (EditText) findViewById(R.id.form_phone);

        sacPhoneField = (EditText) findViewById(R.id.form_sac_phone);
        emailField = (EditText) findViewById(R.id.form_email);
        websiteField = (EditText) findViewById(R.id.form_website);
        whatsappField = (EditText) findViewById(R.id.form_whatsapp);
        facebookField = (EditText) findViewById(R.id.form_facebook);
        instagramField = (EditText) findViewById(R.id.form_instagram);

        inputLayoutName = (TextInputLayout) findViewById(R.id.form_name_layout);
        inputLayoutDescrption = (TextInputLayout) findViewById(R.id.form_description_layout);
        inputLayoutCorporateNumber = (TextInputLayout) findViewById(R.id.form_corporate_number_layout);


        inputLayoutCity = (TextInputLayout) findViewById(R.id.form_city_layout);
        inputLayoutDistrict = (TextInputLayout) findViewById(R.id.form_district_layout);
        inputLayoutStreet = (TextInputLayout) findViewById(R.id.form_street_layout);
        inputLayoutBusinessHours = (TextInputLayout) findViewById(R.id.form_business_hours_layout);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.form_phone_layout);

        inputLayoutSacPhone = (TextInputLayout) findViewById(R.id.form_sac_phone_layout);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.form_email_layout);
        inputLayoutWebsite = (TextInputLayout) findViewById(R.id.form_website_layout);
        inputLayoutWhatsapp = (TextInputLayout) findViewById(R.id.form_whatsapp_layout);
        inputLayoutFacebook = (TextInputLayout) findViewById(R.id.form_facebook_layout);
        inputLayoutInstagram = (TextInputLayout) findViewById(R.id.form_instagram_layout);

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

        String category = categorySelection.getSelectedItem().toString();
        String state = stateSelection.getSelectedItem().toString();

        if(!validateName(name)){
            return;
        }
        if(!validateDescription(description)){
            return;
        }
        if(!validateCorporateNumber(corporateNumber)){
            return;
        }

        if(!validateCategory(category)){
            return;
        }
        if(!validateState(state, city)){
            return;
        }
        if(!validatePhone(phoneNumber)){
            return;
        }
        if(!validatePhone(sacPhone)){
            return;
        }
        if(!validateEmail(email)){
            return;
        }
        if(!validateWebsite(website)){
            return;
        }
        if(!validateWhatsApp(whatsapp)){
            return;
        }
        if(!validateFacebookPage(facebook)){
            return;
        }
        if(!validateInstagramPage(instagram)){
            return;
        }

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

    //Mandatory
    private boolean validateName(String name){
        if (!ValidationTools.isValidName(name)){
            inputLayoutName.setError(getString(R.string.err_msg_bus_name));
            requestFocus(nameField);
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
            requestFocus(descriptionField);
            return false;
        }else {
            inputLayoutDescrption.setErrorEnabled(false);
        }
        return  true;
    }

    private boolean validateCorporateNumber(String corporateNumber){

        inputLayoutCorporateNumber.setErrorEnabled(false);
        return  true;
    }

    private boolean validateCategory(String category){
        if (category.equals(getString(R.string.prompt_category))){
            Toast.makeText(CreateBusinessActivity.this, R.string.err_msg_category, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateState(String state, String city){
        if (state.equals(getString(R.string.prompt_uf)) &&  ! city.isEmpty()){
            Toast.makeText(CreateBusinessActivity.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePhone(String phone){
        //TODO: Check how to validate a phone and give visual feedback
        return true;
    }

    private boolean validateEmail(String email){
        if(!ValidationTools.isValidEmail(email)){
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(emailField);
            return false;
        }else{
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateWebsite(String page){
        inputLayoutWebsite.setErrorEnabled(false);
        return true;
    }
    private boolean validateWhatsApp(String page){
        inputLayoutWhatsapp.setErrorEnabled(false);
        return true;
    }
    private boolean validateFacebookPage(String page){
        inputLayoutFacebook.setErrorEnabled(false);
        return true;
    }

    private boolean validateInstagramPage(String page){
        inputLayoutInstagram.setErrorEnabled(false);
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
