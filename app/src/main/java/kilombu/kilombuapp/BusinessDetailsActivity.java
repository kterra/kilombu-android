package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
    LinearLayout linearLayout;
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
                    boolean allEmptyFlag = true;
                    TextView currentText;
                    if (businessDetails.getStores() != null){
                        Map<String, Store> stores = businessDetails.getStores();
                        for (Store store : stores.values()){
                            //TODO: deal with many stores and phone number



                            String address = store.getAddress().toString();

                            if(address != null && !address.isEmpty()) {
                                allEmptyFlag = false;
                                currentText = (TextView) findViewById(R.id.business_address);
                                currentText.setText(address);
                                currentText.setVisibility(View.VISIBLE);

                            }

                            String workingHours = store.getBusinessHours();
                            if(workingHours != null && !workingHours.isEmpty()){
                                allEmptyFlag = false;
                                currentText = (TextView) findViewById(R.id.business_working_hours);
                                currentText.setText(workingHours);
                                currentText.setVisibility(View.VISIBLE);
                            }


                            String phone = store.getPhoneNumber();
                            if(phone != null && !phone.isEmpty()) {
                                allEmptyFlag = false;
                                currentText = (TextView) findViewById(R.id.business_phone_number);
                                currentText.setText("Tel: " + phone);
                                currentText.setVisibility(View.VISIBLE);
                            }



                        }
                    }else{
                        Log.d("flag", Boolean.toString(allEmptyFlag));
                        currentText = (TextView) findViewById(R.id.noinfo_message);
                        currentText.setVisibility(View.VISIBLE);
                    }
                    
                    boolean sacAllEmptyFlag = true;

                    if (businessDetails.getSACNumber() != null &&
                            !businessDetails.getSACNumber().isEmpty()){
                        sacAllEmptyFlag = false;
                       linearLayout  = (LinearLayout) findViewById(R.id.gruop1);
                        linearLayout .setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_sac_phone);
                        currentText.setText(businessDetails.getSACNumber());
                    }

                    /**TODO: Must setup the images so they can send an intent for each app**/
                    if (businessDetails.getEmail() != null &&
                            !businessDetails.getEmail().isEmpty()){
                        sacAllEmptyFlag = false;
                        linearLayout  = (LinearLayout) findViewById(R.id.gruop2);
                        linearLayout .setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_email);
                        currentText.setText(businessDetails.getEmail());
                    }

                    if (businessDetails.getWebsite() != null &&
                            !businessDetails.getWebsite().isEmpty()){
                        sacAllEmptyFlag = false;
                        linearLayout  = (LinearLayout) findViewById(R.id.gruop3);
                        linearLayout .setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_website);
                        currentText.setText(businessDetails.getWebsite());
                    }

                    if (businessDetails.getWhatsapp() != null &&
                            !businessDetails.getWebsite().isEmpty()){
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop4);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_whatsapp);
                        currentText.setText(businessDetails.getWhatsapp());
                    }

                    if (businessDetails.getFacebookPage() != null &&
                            !businessDetails.getFacebookPage().isEmpty()){
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop5);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_facebook);
                        currentText.setText(businessDetails.getFacebookPage());
                    }

                    if (businessDetails.getInstagramPage() != null &&
                            !businessDetails.getInstagramPage().isEmpty()){
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop6);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_instagram);
                        currentText.setText(businessDetails.getInstagramPage());
                    }
                    Log.d("flag_sac_before", Boolean.toString(sacAllEmptyFlag));

                    if (sacAllEmptyFlag == true){
                        Log.d("flag_sac", Boolean.toString(sacAllEmptyFlag));
                        currentText = (TextView) findViewById(R.id.no_info_message);
                        currentText.setVisibility(View.VISIBLE);
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
