package kilombu.kilombuapp;

import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class BusinessDetailsActivity extends AppCompatActivity {

    private Firebase detailsRef;
    private final String TAG = "Business Details";
    String businessId;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        checkInternetConnection();

        detailsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_details));
        setupBusinessDetails();
    }


    public String setupBusinessDetails() {
        Intent intent = getIntent();
        TextView currentText = (TextView) findViewById(R.id.business_name_detail);
        currentText.setText(intent.getStringExtra("business_name"));
        currentText = (TextView) findViewById(R.id.business_description_detail);
        currentText.setText(intent.getStringExtra("business_description"));

        final ProgressBar contactsLoading = (ProgressBar) findViewById(R.id.details_contacts_loading);
        contactsLoading.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
        contactsLoading.setVisibility(View.VISIBLE);
        final ProgressBar storeLoading = (ProgressBar) findViewById(R.id.details_store_loading);
        storeLoading.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
        storeLoading.setVisibility(View.VISIBLE);

        businessId = intent.getStringExtra("businessId");

        Query detailsQuery = detailsRef.orderByKey().equalTo(businessId).limitToFirst(1);
        detailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    BusinessDetails businessDetails = dataSnapshot.getChildren().iterator().
                            next().getValue(BusinessDetails.class);
                    //TODO: configure details cell

                    TextView currentText;
                    if (businessDetails.getStores() != null) {
                        Map<String, Store> stores = businessDetails.getStores();
                        for (Store store : stores.values()) {
                            //TODO: deal with many stores and phone number

                            String address = store.getAddress().toString();

                            if (address != null && !address.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.business_address);
                                currentText.setText(address);
                                currentText.setVisibility(View.VISIBLE);

                            }

                            String workingHours = store.getBusinessHours();
                            if (workingHours != null && !workingHours.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.business_working_hours);
                                currentText.setText(workingHours);
                                currentText.setVisibility(View.VISIBLE);
                            }


                            String phone = store.getPhoneNumber();
                            if (phone != null && !phone.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.business_phone_number);
                                currentText.setText("Tel: " + phone);
                                currentText.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        Log.d("flag", "true");
                        currentText = (TextView) findViewById(R.id.noinfo_message);
                        currentText.setVisibility(View.VISIBLE);
                    }
                    storeLoading.setVisibility(View.GONE);

                    boolean sacAllEmptyFlag = true;

                    if (businessDetails.getSacNumber() != null &&
                            !businessDetails.getSacNumber().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop1);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_sac_phone);
                        currentText.setText(businessDetails.getSacNumber());
                    }

                    /**TODO: Must setup the images so they can send an intent for each app**/
                    if (businessDetails.getEmail() != null &&
                            !businessDetails.getEmail().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop2);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_email);
                        currentText.setText(businessDetails.getEmail());
                    }

                    if (businessDetails.getWebsite() != null &&
                            !businessDetails.getWebsite().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop3);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_website);
                        currentText.setText(businessDetails.getWebsite());
                    }

                    if (businessDetails.getWhatsapp() != null &&
                            !businessDetails.getWebsite().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop4);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_whatsapp);
                        currentText.setText(businessDetails.getWhatsapp());
                    }

                    if (businessDetails.getFacebookPage() != null &&
                            !businessDetails.getFacebookPage().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop5);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_facebook);
                        currentText.setText(businessDetails.getFacebookPage());
                    }

                    if (businessDetails.getInstagramPage() != null &&
                            !businessDetails.getInstagramPage().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.gruop6);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.business_instagram);
                        currentText.setText(businessDetails.getInstagramPage());
                    }
                    Log.d("flag_sac_before", Boolean.toString(sacAllEmptyFlag));

                    if (sacAllEmptyFlag == true) {
                        Log.d("flag_sac", Boolean.toString(sacAllEmptyFlag));
                        currentText = (TextView) findViewById(R.id.no_info_message);
                        currentText.setVisibility(View.VISIBLE);
                    }
                    contactsLoading.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(BusinessDetailsActivity.this,
                        "An error ocureed during details retrieval", Toast.LENGTH_LONG);
            }
        });

        updateVisualizationsCounter();

        return businessId;
    }

    private void updateVisualizationsCounter() {
        Firebase viewsCountRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_statistics)).child(businessId)
                .child(getString(R.string.child_statistics_visualizations));

        viewsCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
                //TODO: do we need to do anything here?
            }
        });
    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return new Boolean(true);
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    private void checkInternetConnection() {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;

            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                if(isOnline()){

                }else{
                    Toast.makeText(BusinessDetailsActivity.this, "Sme internet", Toast.LENGTH_SHORT);
                }
            }
        }.start();
    }
}

