package kilombu.kilombuapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class CreateBusinessActivity extends AppCompatActivity {

    private Firebase appRef;
    private Firebase businessRef;
    private Firebase detailsRef;
    private Spinner categorySelection;
    private Spinner stateSelection;
    private int storeIndex = 1;
    private boolean isTransition = false;
    private EditText nameField, descriptionField, corporateNumberField, cityField, streetField,
            complementField, districtField, businessHoursField,phoneField, sacPhoneField, emailField,
            websiteField, whatsappField, facebookField, instagramField;
    private TextInputLayout inputLayoutName, inputLayoutDescription, inputLayoutCorporateNumber,
            inputLayoutCity, inputLayoutDistrict, inputLayoutStreet,  inputLayoutBusinessHours, inputLayoutPhone,
            inputLayoutSacPhone, inputLayoutEmail, inputLayoutWebsite, inputLayoutWhatsapp,
            inputLayoutFacebook, inputLayoutInstagram;
    private SharedPreferences busPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_create_business));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameField = (EditText) findViewById(R.id.form_name);
        descriptionField = (EditText) findViewById(R.id.form_description);
        corporateNumberField = (EditText) findViewById(R.id.form_corporate_number);

        cityField = (EditText) findViewById(R.id.form_city);
        streetField = (EditText) findViewById(R.id.form_street);
        complementField = (EditText) findViewById(R.id.form_complement);
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
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.form_description_layout);
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

    //Floating send button action
    public void sendForm(View sendButton){


        String name = nameField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        String corporateNumber = corporateNumberField.getText().toString().trim();

        String city = cityField.getText().toString().trim();
        String street = streetField.getText().toString().trim();
        String complement = complementField.getText().toString().trim();
        String district = districtField.getText().toString().trim();
        String businessHours = businessHoursField.getText().toString().trim();
        String phoneNumber = phoneField.getText().toString().trim();

        String sacPhone = sacPhoneField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String website = websiteField.getText().toString().trim();
        String whatsapp = whatsappField.getText().toString().trim();
        String facebook = facebookField.getText().toString().trim();
        String instagram = instagramField.getText().toString().trim();

        String category = categorySelection.getSelectedItem().toString().trim();
        String state = stateSelection.getSelectedItem().toString().trim();

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
        if(!validateCity(city)){
            return;
        }
        if(!validateDistrict(district)){
            return;
        }
        if(!validateStreet(street)){
            return;
        }
        if(!validateBusinessHours(businessHours)){
            return;
        }
        if(!validatePhone(phoneNumber)) {
            return;
        }
        if(!validateSacPhone(sacPhone)){
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

        String admin = appRef.getAuth().getUid();
        int categoryIndex = ValidationTools.convertCategory(category, this);
        Business business = new Business(name, admin, categoryIndex, description, corporateNumber);

        Firebase newBusinessRef = businessRef.push();
        final String businessId = newBusinessRef.getKey();
        newBusinessRef.setValue(business);

        BusinessAddress address = ValidationTools.validateAddress(street, complement, district, city, state);
        Store store = ValidationTools.validateStore(address, phoneNumber, businessHours);
        //TODO: check if we need to ask for a unit name
        Map<String, Store> stores = null;
        if (store != null){
            stores = new HashMap<String, Store>();
            stores.put(Integer.toString(storeIndex++) + " " + district, store);
        }
        //TODO: check if there exists already a unit in the district and add an index
        BusinessDetails details = new BusinessDetails(stores, sacPhone, email,
                                website, whatsapp, facebook, instagram);

        detailsRef.child(businessId).setValue(details);
        createBusinessStatistics(businessId);
        //Intent intent = new Intent(CreateBusinessActivity.this, MainActivity.class);
        isTransition = true;
        finish();
        Toast.makeText(getApplicationContext(), "Cadastro efetuado com sucesso!", Toast.LENGTH_LONG).show();
        //startActivity(intent);

            /*detailsRef.child(businessId).setValue(details, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    createBusinessStatistics(businessId);
                    Toast.makeText(getApplicationContext(), "Cadastro efetuado com sucesso!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateBusinessActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });*/



        busPreferences = CreateBusinessActivity.this.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);

        SharedPreferences.Editor busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessid_key), businessId);
        busEditor.putString(getString(R.string.businessadmin_key), admin);
        busEditor.putString(getString(R.string.businessname_key),name);
        busEditor.putString(getString(R.string.businessdescription_key), description);
        busEditor.putString(getString(R.string.businesscorpnumber_key), corporateNumber);
        busEditor.putString(getString(R.string.businesscategory_key), category);
        busEditor.commit();

    }

    private void createBusinessStatistics(String businessId){
        Firebase statisticsRef = appRef.child(getString(R.string.child_business_statistics))
                                .child(businessId);
        statisticsRef.setValue(new BusinessStatistics());
        statisticsRef.child(getString(R.string.child_statistics_timestamp))
                            .setValue(ServerValue.TIMESTAMP);
    }

    //Mandatory
    private boolean validateName(String name){
        if (!ValidationTools.isValidName(name)){
            inputLayoutName.setError(getString(R.string.err_msg_bus_name));
            requestFocus(nameField);
            Toast.makeText(CreateBusinessActivity.this, R.string.err_msg_toast, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    //Mandatory
    private boolean validateDescription(String description){
        if (!ValidationTools.isValidDescription(description)){
            inputLayoutDescription.setError(getString(R.string.err_msg_description));
            requestFocus(descriptionField);
            Toast.makeText(CreateBusinessActivity.this, R.string.err_msg_toast, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            inputLayoutDescription.setErrorEnabled(false);
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

    private boolean validateCity(String city){
        inputLayoutCity.setErrorEnabled(false);
        return true;
    }

    private boolean validateDistrict(String district){
        inputLayoutDistrict.setErrorEnabled(false);
        return true;
    }

    private boolean validateStreet(String street){
        inputLayoutStreet.setErrorEnabled(false);
        return true;
    }

    private boolean validateBusinessHours(String businessHours){
        inputLayoutBusinessHours.setErrorEnabled(false);
        return true;
    }

    private boolean validatePhone(String phone){
        inputLayoutPhone.setErrorEnabled(false);
        return true;
    }

    private boolean validateSacPhone(String phone){
        inputLayoutSacPhone.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmail(String email){
        if(!email.isEmpty() && !ValidationTools.isValidEmail(email)){
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            Toast.makeText(CreateBusinessActivity.this, R.string.err_msg_toast, Toast.LENGTH_LONG).show();
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
