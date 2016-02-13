package kilombu.kilombuapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class CreateBusinessActivity extends AppCompatActivity {

    Firebase appRef;
    Firebase businessRef;
    Firebase detailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appRef = new Firebase(getString(R.string.firebase_url));
        businessRef = appRef.child("business");
        detailsRef = appRef.child("BusinessDetails");

    }

    //Floating send button action
    public void sendForm(View sendButton){
        EditText nameField = (EditText) findViewById(R.id.form_name);
        EditText emailField = (EditText) findViewById(R.id.form_email);

        EditText cityField = (EditText) findViewById(R.id.form_city);
        EditText streetField = (EditText) findViewById(R.id.form_street);
        EditText districtField = (EditText) findViewById(R.id.form_district);
        EditText phoneField = (EditText) findViewById(R.id.form_phone);

        EditText descriptionField = (EditText) findViewById(R.id.form_description);
        EditText businessHoursField = (EditText) findViewById(R.id.form_business_hours);
        EditText facebookField = (EditText) findViewById(R.id.form_facebook);
        EditText instagramField = (EditText) findViewById(R.id.form_instagram);

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String description = descriptionField.getText().toString();

        String city = cityField.getText().toString();
        String street = streetField.getText().toString();
        String district = districtField.getText().toString();
        String businessHours = businessHoursField.getText().toString();
        String phoneNumber = phoneField.getText().toString();

        String facebook = facebookField.getText().toString();
        String instagram = instagramField.getText().toString();

        //TODO: deal with category as droplist or similar
        String category = "dummy";

        if (dataIsValid(name, email, city, phoneNumber, description, businessHours, facebook, instagram)){

            BusinessAddress address = new BusinessAddress(city, street, district);
            Store store = new Store(address, phoneNumber, businessHours);

            String admin = appRef.getAuth().getUid();
            Business business = new Business(name, admin, category, description, "0000000000/00");


            Firebase newBusinessRef = businessRef.push();
            String businessId = newBusinessRef.getKey();
            newBusinessRef.setValue(business);

            //TODO: check if we need to ask for a unit name
            Map<String, Store> stores = new HashMap<String, Store>();
            //TODO: check if there exists already a unit in the district and add an index
            stores.put(district, store);
            BusinessDetails details = new BusinessDetails(stores, email, facebook,
                    instagram, null, null, null);

            detailsRef.child(businessId).setValue(details);

        }

    }


    //TODO: separate validation methods
    private boolean dataIsValid(String name, String email, String city,
                                String phoneNumber, String description, String businessHours,
                                String facebook, String instagram){

        return true;
    }




}
