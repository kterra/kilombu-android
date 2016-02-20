package kilombu.kilombuapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class BusinessProfileActivity extends AppCompatActivity {
    private Firebase appRef;
    private String businessId;
    private BusinessDetails currentDetails;
    private BusinessStatistics currentStatistics;
    private SharedPreferences busPreferences;
    private android.content.Context context;
    private boolean sacAllEmptyFlag;

    private final String TAG = "Business Profile";
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_business_profile));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appRef = new Firebase(getString(R.string.firebase_url));

        context = BusinessProfileActivity.this;
        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        setupBusinessCard();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Firebase.goOnline();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Firebase.goOffline();
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

        if (! KilombuApplication.hasActiveNetworkConnection(getApplicationContext())){

            Toast.makeText(BusinessProfileActivity.this, "Sem internet, Doido!", Toast.LENGTH_SHORT).show();
        }

        setupBusinessDetailsCard();
        setupStatisticsCards();

    }

    private void setupBusinessDetailsCard(){

        final ProgressBar contactsLoading = (ProgressBar) findViewById(R.id.profile_contacts_loading);
        contactsLoading.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
        contactsLoading.setVisibility(View.VISIBLE);

        final ProgressBar storeLoading = (ProgressBar) findViewById(R.id.profile_store_loading);
        storeLoading.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
        storeLoading.setVisibility(View.VISIBLE);

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
                        currentText = (TextView) findViewById(R.id.profile_no_storesinfo_message);
                        currentText.setVisibility(View.VISIBLE);
                    }

                    storeLoading.setVisibility(View.GONE);
                   sacAllEmptyFlag = true;
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
                    contactsLoading.setVisibility(View.GONE);
                } else {
                    Toast.makeText(BusinessProfileActivity.this, getString(R.string.toast_no_datasnapshot), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(BusinessProfileActivity.this,
                        getString(R.string.toast_no_datasnapshot), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupStatisticsCards(){

        final ProgressBar statisticsLoading = (ProgressBar) findViewById(R.id.profile_statistics_loading);
        statisticsLoading.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
        statisticsLoading.setVisibility(View.VISIBLE);

        Firebase statisticsRef = appRef.child(getString(R.string.child_business_statistics));
        Query statisticsQuery = statisticsRef.orderByKey().equalTo(businessId);
        statisticsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    statisticsLoading.setVisibility(View.GONE);
                    currentStatistics = dataSnapshot.getChildren().iterator()
                            .next().getValue(BusinessStatistics.class);
                    TextView viewsCount = (TextView) findViewById(R.id.profile_number_view);
                    viewsCount.setText(Long.toString(currentStatistics.getVisualizations()));
                    viewsCount = (TextView) findViewById(R.id.profile_number_likes);
                    viewsCount.setText(Long.toString(currentStatistics.getRecommendations()));


                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(currentStatistics.getTimestamp());
                    String registrationDate = DateFormat.format("dd-MM-yyyy", cal).toString();
                    ((TextView) findViewById(R.id.business_timestamp)).setText("Na família Kilombu desde: " + registrationDate);

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
        intent.putExtra("businessId", businessId);

        //TODO: find a new way when we support multiple stores

        if(currentDetails.getStores() != null){
            Store currentStore = currentDetails.getStores().values().iterator().next();
            BusinessAddress currentAddress = currentStore.getAddress();

            intent.putExtra("empty", "false");
            intent.putExtra(getString(R.string.child_details_store_address_country), currentAddress.getCountry());
            intent.putExtra(getString(R.string.child_details_store_address_state), currentAddress.getState());
            intent.putExtra(getString(R.string.child_details_store_address_city), currentAddress.getCity());
            intent.putExtra(getString(R.string.child_details_store_address_district), currentAddress.getDistrict());
            intent.putExtra(getString(R.string.child_details_store_address_street), currentAddress.getStreet());
            intent.putExtra(getString(R.string.child_details_store_address_complement), currentAddress.getComplement());
            intent.putExtra(getString(R.string.child_details_store_phone), currentStore.getPhoneNumber());
            intent.putExtra(getString(R.string.child_details_store_business_hours), currentStore.getBusinessHours());

        }
        startActivity(intent);

    }

    public void removeBusiness(View button){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_report_problem_black_24dp)
                .setTitle(getString(R.string.remove_business))
                .setMessage(getString(R.string.remove_business_msg))
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
