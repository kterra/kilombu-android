package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class BusinessDetailsActivity extends AppCompatActivity {

    private Firebase detailsRef;
    private final String TAG = "Business Details";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        detailsRef = new Firebase(getString(R.string.firebase_url)).child("BusinessDetails");
        setupBusinessDetails();

    }


    public String setupBusinessDetails(){
        Intent intent = getIntent();
        TextView currentText = (TextView) findViewById(R.id.business_name_detail);
        currentText.setText(intent.getStringExtra("business_name"));
        currentText = (TextView) findViewById(R.id.business_description_detail);
        currentText.setText(intent.getStringExtra("business_description"));

        String businessKey = intent.getStringExtra("business_key");
        Query detailsQuery = detailsRef.orderByKey().equalTo(businessKey).limitToFirst(1);
        detailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    BusinessDetails businessDetails = dataSnapshot.getChildren().iterator().
                            next().getValue(BusinessDetails.class);
                    //TODO: configure details cell
                    TextView currentText;

                    if (businessDetails.getStores() != null){
                        Map<String, Store> stores = businessDetails.getStores();
                        for (Store store : stores.values()){
                            //TODO: deal with many stores and phone number
                            currentText = (TextView) findViewById(R.id.business_address);
                            currentText.setText(store.getAddress().toString());

                            currentText = (TextView) findViewById(R.id.business_working_hours);
                            currentText.setText(store.getBusinessHours());

                        }
                    }

                    if (businessDetails.getSACNumber() != null &&
                            !businessDetails.getSACNumber().isEmpty()){
                        currentText = (TextView) findViewById(R.id.business_phone_number);
                        currentText.setText("Tel: " + businessDetails.getSACNumber());
                    }

                    /**TODO: Must setup the images so they can send an intent for each app**/
                    if (businessDetails.getEmail() != null &&
                            !businessDetails.getEmail().isEmpty()){
                        //TODO: put email reference
                    }

                    if (businessDetails.getFacebookPage() != null &&
                            !businessDetails.getFacebookPage().isEmpty()){
                        //TODO: put facebook logo referencing facebook page
                    }

                    if (businessDetails.getInstagramPage() != ""){
                        //TODO: put instagram logo referencing instagram page
                    }

                    if (businessDetails.getWebsite() != null &&
                            !businessDetails.getWebsite().isEmpty()){
                        //TODO: put website reference
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(BusinessDetailsActivity.this,
                        "An error ocureed during details retrieval", Toast.LENGTH_LONG);
            }
        });



        return businessKey;
    }
}
