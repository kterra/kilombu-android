package kilombu.kilombuapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class CreateBusinessActivity extends AppCompatActivity {

    Firebase appRef;
    Firebase businessRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        appRef = new Firebase(getString(R.string.firebase_url));
        businessRef = appRef.child("business");

    }

    //Floating send button action
    public void sendForm(View sendButton){
        EditText nameField = (EditText) findViewById(R.id.form_name);
        EditText emailField = (EditText) findViewById(R.id.form_email);
        EditText addressField = (EditText) findViewById(R.id.form_address);
        EditText cityField = (EditText) findViewById(R.id.form_city);
        EditText descriptionField = (EditText) findViewById(R.id.form_description);
        EditText businessHoursField = (EditText) findViewById(R.id.form_business_hours);
        EditText facebookField = (EditText) findViewById(R.id.form_facebook);
        EditText instagramField = (EditText) findViewById(R.id.form_instagram);





    }




}
