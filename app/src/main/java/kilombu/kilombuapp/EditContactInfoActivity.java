package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class EditContactInfoActivity extends AppCompatActivity {

    String businessId, email, website, sacNumber, whatsapp,facebookPage, instagramPage;
    private Map<String, Object> contactInfoUpdates;
    private TextInputLayout inputLayoutSacPhone, inputLayoutEmail, inputLayoutWebsite, inputLayoutWhatsapp,
            inputLayoutFacebook, inputLayoutInstagram;
    private SharedPreferences busPreferences;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = EditContactInfoActivity.this;

        contactInfoUpdates = new HashMap<String, Object>();
        setupContactInfo();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateInfo();
            }
        });

    }

    private void setupContactInfo(){

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        businessId = busPreferences.getString(getString(R.string.businessid_key), "");
        email = busPreferences.getString(getString(R.string.businessdetails_email_key), "");
        website = busPreferences.getString(getString(R.string.businessdetails_website_key), "");
        sacNumber = busPreferences.getString(getString(R.string.businessdetails_sacnumber_key), "");
        whatsapp = busPreferences.getString(getString(R.string.businessdetails_whatsapp_key), "");
        facebookPage = busPreferences.getString(getString(R.string.businessdetails_facebook_key), "");
        instagramPage = busPreferences.getString(getString(R.string.businessdetails_instagram_key), "");

        EditText currentText = (EditText) findViewById(R.id.edit_email);
        currentText.setText(email);

        currentText = (EditText) findViewById(R.id.edit_website);
        currentText.setText(website);

        currentText = (EditText) findViewById(R.id.edit_sac_phone);
        currentText.setText(sacNumber);

        currentText = (EditText) findViewById(R.id.edit_whatsapp);
        currentText.setText(whatsapp);

        currentText = (EditText) findViewById(R.id.edit_facebook);
        currentText.setText(facebookPage);

        currentText = (EditText) findViewById(R.id.edit_instagram);
        currentText.setText(instagramPage);
    }


    public void saveAndUpdateInfo(){


        EditText currentText = (EditText) findViewById(R.id.edit_email);
        email = currentText.getText().toString();
        if(!validateEmail(email)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_email), email);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_email_key), email);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_website);
        website = currentText.getText().toString();
        if(!validateWebsite(website)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_website), website);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_website_key), website);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_sac_phone);
        sacNumber =  currentText.getText().toString();
        if(!validateSacPhone(sacNumber)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_sac), sacNumber);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_sacnumber_key), sacNumber);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_whatsapp);
        whatsapp = currentText.getText().toString();
        if(!validateWhatsApp(whatsapp)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_whatsapp), whatsapp);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_whatsapp_key), whatsapp);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_facebook);
        facebookPage = currentText.getText().toString();
        if(!validateFacebookPage(facebookPage)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_facebook), facebookPage);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_facebook_key), facebookPage);
        busEditor.commit();

        currentText = (EditText) findViewById(R.id.edit_instagram);
        instagramPage = currentText.getText().toString();
        if(!validateInstagramPage(instagramPage)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_instagram), instagramPage);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        busEditor = busPreferences.edit();
        busEditor.putString(getString(R.string.businessdetails_instagram_key), instagramPage);
        busEditor.commit();

        Firebase currentDetailsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_details)).child(businessId);
        currentDetailsRef.updateChildren(contactInfoUpdates);

        Intent intent = new Intent(this, BusinessProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean validateEmail(String email) {

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.edit_email_layout);

        if (!ValidationTools.isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus((EditText) findViewById(R.id.new_useremail));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateWebsite(String page){
        inputLayoutWebsite = (TextInputLayout) findViewById(R.id.edit_website_layout);
        inputLayoutWebsite.setErrorEnabled(false);
        return true;
    }

    private boolean validateSacPhone(String phone){
        inputLayoutSacPhone = (TextInputLayout) findViewById(R.id.edit_sac_phone_layout);
        inputLayoutSacPhone.setErrorEnabled(false);
        return true;
    }

    private boolean validateWhatsApp(String page){
        inputLayoutWhatsapp = (TextInputLayout) findViewById(R.id.edit_whatsapp_layout);
        inputLayoutWhatsapp.setErrorEnabled(false);
        return true;
    }
    private boolean validateFacebookPage(String page){
        inputLayoutFacebook = (TextInputLayout) findViewById(R.id.edit_facebook_layout);
        inputLayoutFacebook.setErrorEnabled(false);
        return true;
    }

    private boolean validateInstagramPage(String page){
        inputLayoutInstagram = (TextInputLayout) findViewById(R.id.edit_instagram_layout);;
        inputLayoutInstagram.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
