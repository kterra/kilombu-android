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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

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
        String businessName = intent.getStringExtra("business_name");
        currentText.setText(intent.getStringExtra("business_name"));

        String businessKey = intent.getStringExtra("business_key");
        Log.d(TAG, businessKey);

        Query detailsQuery = detailsRef.orderByKey().equalTo(businessKey);
        detailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    BusinessDetails businessDetails = dataSnapshot.getChildren().iterator().
                            next().getValue(BusinessDetails.class);
                    TextView currentText = (TextView) findViewById(R.id.business_working_hours);
                    currentText.setText(businessDetails.getFacebookPage());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        currentText = (TextView) findViewById(R.id.business_description_detail);
        currentText.setText(intent.getStringExtra("business_description"));

        currentText = (TextView) findViewById(R.id.business_address);
        currentText.setText("Rua das Coves, 145");

        currentText = (TextView) findViewById(R.id.business_phone_number);
        currentText.setText("Tel: 021 3234-5678");

        //currentText = (TextView) findViewById(R.id.business_working_hours);
        //currentText.setText("Segunda a Sexta: 08:00 — 12:00");

        /**Must setup the images so they can send an intent for each app**/

        return businessName;
    }
}
