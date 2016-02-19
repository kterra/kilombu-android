package kilombu.kilombuapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BusinessProfileActivity extends AppCompatActivity {
    private Firebase appRef;
    private String businessId;
    private Business currentBusiness;
    private BusinessDetails currentDetails;
    private BusinessStatistics currentStatistics;
    private Map<String, Object> updatesOnBusiness;
    private Map<String, Object> updatesOnDetails;
    private SharedPreferences busPreferences;
    private android.content.Context context;

    private final String TAG = "Business Profile";
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_business_profile));

        appRef = new Firebase(getString(R.string.firebase_url));

        context = BusinessProfileActivity.this;
        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        setupBusinessCard();

        updatesOnBusiness = new HashMap<String, Object>();
        updatesOnDetails = new HashMap<String, Object>();

    }


    private void setupBusinessCard(){

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        businessId = busPreferences.getString(getString(R.string.businessid_key), "");
        String businessName = busPreferences.getString(getString(R.string.businessname_key), "");
        String businessDescription = busPreferences.getString(getString(R.string.businessdescription_key), "");

        TextView currentText = (TextView) findViewById(R.id.profile_business_name);
        currentText.setText(businessName);
        currentText = (TextView) findViewById(R.id.profile_business_description);
        currentText.setText(businessDescription);

        setupBusinessDetailsCard();
        setupStatisticsCards();

    }

    private void setupBusinessDetailsCard(){
        Firebase detailsRef = appRef.child(getString(R.string.child_business_details));
        Query detailsQuery = detailsRef.orderByKey().equalTo(businessId).limitToFirst(1);
        detailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentDetails = dataSnapshot.getChildren().iterator().
                            next().getValue(BusinessDetails.class);
                    //TODO: configure details cell

                    TextView currentText;

                    if (currentDetails.getStores() != null) {
                        Map<String, Store> stores = currentDetails.getStores();
                        for (Store store : stores.values()) {
                            //TODO: deal with many stores and phone number

                            String address = store.getAddress().toString();

                            if (address != null && !address.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.profile_business_address);
                                currentText.setText(address);
                                currentText.setVisibility(View.VISIBLE);


                            }

                            String workingHours = store.getBusinessHours();
                            if (workingHours != null && !workingHours.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.profile_business_working_hours);
                                currentText.setText(workingHours);
                                currentText.setVisibility(View.VISIBLE);
                            }


                            String phone = store.getPhoneNumber();
                            if (phone != null && !phone.isEmpty()) {
                                currentText = (TextView) findViewById(R.id.profile_business_phone_number);
                                currentText.setText("Tel: " + phone);
                                currentText.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        Log.d("flag", "true");
                        currentText = (TextView) findViewById(R.id.profile_no_storesinfo_message);
                        currentText.setVisibility(View.VISIBLE);
                    }

                    boolean sacAllEmptyFlag = true;
                    String sacNumber = currentDetails.getSacNumber();
                    if (sacNumber != null &&
                            !sacNumber.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group1);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_sac_phone);
                        currentText.setText(sacNumber);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_sacnumber_key), sacNumber);
                        busEditor.commit();

                    }

                    String email = currentDetails.getEmail();
                    /**TODO: Must setup the images so they can send an intent for each app**/
                    if (email != null &&
                            !email.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group2);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_email);
                        currentText.setText(email);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_email_key), email);
                        busEditor.commit();
                    }

                    String website = currentDetails.getWebsite();
                    if (website != null &&
                            !website.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group3);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_website);
                        currentText.setText(website);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_website_key), website);
                        busEditor.commit();
                    }

                    String whatsapp = currentDetails.getWhatsapp();
                    if (whatsapp != null &&
                            !whatsapp.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group4);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_whatsapp);
                        currentText.setText(whatsapp);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_whatsapp_key), whatsapp);
                        busEditor.commit();
                    }

                    String facebookPage = currentDetails.getFacebookPage();
                    if (facebookPage != null &&
                            !facebookPage.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group5);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_facebook);
                        currentText.setText(currentDetails.getFacebookPage());

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_facebook_key), facebookPage);
                        busEditor.commit();
                    }

                    String instagramPage = currentDetails.getInstagramPage();
                    if (instagramPage != null &&
                            !instagramPage.isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group6);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_instagram);
                        currentText.setText(instagramPage);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessdetails_instagram_key), instagramPage);
                        busEditor.commit();
                    }
                    Log.d("flag_sac_before", Boolean.toString(sacAllEmptyFlag));

                    if (sacAllEmptyFlag == true) {
                        Log.d("flag_sac", Boolean.toString(sacAllEmptyFlag));
                        currentText = (TextView) findViewById(R.id.profile_no_contactinfo_message);
                        currentText.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(BusinessProfileActivity.this, "Nao encontrou o details", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(BusinessProfileActivity.this,
                        "An error ocureed during details retrieval", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupStatisticsCards(){
        Firebase statisticsRef = appRef.child(getString(R.string.child_business_statistics));
        Query statisticsQuery = statisticsRef.orderByKey().equalTo(businessId);
        statisticsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentStatistics = dataSnapshot.getChildren().iterator()
                            .next().getValue(BusinessStatistics.class);
                    TextView viewsCount = (TextView) findViewById(R.id.profile_number_view);
                    viewsCount.setText(Long.toString(currentStatistics.getVisualizations()));
                    viewsCount = (TextView) findViewById(R.id.profile_number_likes);
                    viewsCount.setText(Long.toString(currentStatistics.getRecommendations()));


                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(currentStatistics.getTimestamp());
                    String registrationDate = DateFormat.format("dd-MM-yyyy", cal).toString();
                    ((TextView)findViewById(R.id.business_timestamp)).setText("Na família Kilombu desde: " + registrationDate);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void goToEditBusinessInfo(View editButton){
        Intent intent = new Intent(this, EditBusinessInfoActivity.class);
        startActivity(intent);
    }

    public void goToEditContactInfo(View editButton){
        Intent intent = new Intent(this, EditContactInfoActivity.class);
        startActivity(intent);
    }

    public void goToEditStoreInfo(View editButton){
        Intent intent = new Intent(this, EditStoreInfoActivity.class);
        //TODO: find a new way when we support multiple stores
        Store currentStore = currentDetails.getStores().values().iterator().next();
        BusinessAddress currentAddress = currentStore.getAddress();

        intent.putExtra("businessId", businessId);
        intent.putExtra(getString(R.string.child_details_store_address_country), currentAddress.getCountry());
        intent.putExtra(getString(R.string.child_details_store_address_state), currentAddress.getState());
        intent.putExtra(getString(R.string.child_details_store_address_city), currentAddress.getCity());
        intent.putExtra(getString(R.string.child_details_store_address_district), currentAddress.getDistrict());
        intent.putExtra(getString(R.string.child_details_store_address_street), currentAddress.getStreet());
        intent.putExtra(getString(R.string.child_details_store_phone), currentStore.getPhoneNumber());
        intent.putExtra(getString(R.string.child_details_store_business_hours), currentStore.getBusinessHours());

        startActivity(intent);
    }

    public void removeBusiness(View button){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.remove_business))
                .setMessage(getString(R.string.remove_business_message))
                .setPositiveButton(getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appRef.child(getString(R.string.child_business)).child(businessId).setValue(null);
                        appRef.child(getString(R.string.child_business_details)).child(businessId).setValue(null);
                        appRef.child(getString(R.string.child_business_statistics)).child(businessId).setValue(null);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        busPreferences.edit().clear().commit();

                        Intent intent = new Intent(BusinessProfileActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }

                })
                .setNegativeButton(getString(R.string.alert_dialog_negative), null)
                .show();
    }


}
