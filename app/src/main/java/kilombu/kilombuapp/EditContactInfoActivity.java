package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class EditContactInfoActivity extends AppCompatActivity {

    String businessId;
    private Map<String, Object> contactInfoUpdates;

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
        String email = intent.getStringExtra(getString(R.string.child_details_this_email));
        String website = intent.getStringExtra(getString(R.string.child_details_this_website));
        String sacNumber = intent.getStringExtra(getString(R.string.child_details_this_sac));
        String whatsapp = intent.getStringExtra(getString(R.string.child_details_this_whatsapp));
        String facebookPage = intent.getStringExtra(getString(R.string.child_details_this_facebook));
        String instagramPage = intent.getStringExtra(getString(R.string.child_details_this_instagram));


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
        contactInfoUpdates.put(getString(R.string.child_details_this_email),
                            currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_website);
        contactInfoUpdates.put(getString(R.string.child_details_this_website),
                currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_sac_phone);
        contactInfoUpdates.put(getString(R.string.child_details_this_sac),
                currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_whatsapp);
        contactInfoUpdates.put(getString(R.string.child_details_this_whatsapp),
                currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_facebook);
        contactInfoUpdates.put(getString(R.string.child_details_this_facebook),
                currentText.getText().toString());

        currentText = (EditText) findViewById(R.id.edit_instagram);
        contactInfoUpdates.put(getString(R.string.child_details_this_instagram),
                currentText.getText().toString());

        Firebase currentDetailsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_details)).child(businessId);
        currentDetailsRef.updateChildren(contactInfoUpdates);

        Intent intent = new Intent(this, BusinessProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
