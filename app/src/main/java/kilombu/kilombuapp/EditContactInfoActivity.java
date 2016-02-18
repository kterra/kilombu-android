package kilombu.kilombuapp;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");
        email = intent.getStringExtra(getString(R.string.child_details_this_email));
        website = intent.getStringExtra(getString(R.string.child_details_this_website));
        sacNumber = intent.getStringExtra(getString(R.string.child_details_this_sac));
        whatsapp = intent.getStringExtra(getString(R.string.child_details_this_whatsapp));
        facebookPage = intent.getStringExtra(getString(R.string.child_details_this_facebook));
        instagramPage = intent.getStringExtra(getString(R.string.child_details_this_instagram));


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

        currentText = (EditText) findViewById(R.id.edit_website);
        website = currentText.getText().toString();
        if(!validateWebsite(website)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_website), website);

        currentText = (EditText) findViewById(R.id.edit_sac_phone);
        sacNumber =  currentText.getText().toString();
        if(!validateSacPhone(sacNumber)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_sac), sacNumber);

        currentText = (EditText) findViewById(R.id.edit_whatsapp);
        whatsapp = currentText.getText().toString();
        if(!validateWhatsApp(whatsapp)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_whatsapp), whatsapp);

        currentText = (EditText) findViewById(R.id.edit_facebook);
        facebookPage = currentText.getText().toString();
        if(!validateFacebookPage(facebookPage)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_facebook), facebookPage);

        currentText = (EditText) findViewById(R.id.edit_instagram);
        instagramPage = currentText.getText().toString();
        if(!validateInstagramPage(instagramPage)){
            return;
        }
        contactInfoUpdates.put(getString(R.string.child_details_this_instagram), instagramPage);

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
