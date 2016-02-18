package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class BusinessProfileActivity extends AppCompatActivity {
    private Firebase appRef;
    private String businessId;
    private Business currentBusiness;
    private BusinessDetails currentDetails;
    private BusinessStatistics currentStatistics;
    private Map<String, Object> updatesOnBusiness;
    private Map<String, Object> updatesOnDetails;

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

        //retrieveBusinessdata();

        setupBusinessCard();

        updatesOnBusiness = new HashMap<String, Object>();
        updatesOnDetails = new HashMap<String, Object>();

    }


    private void setupBusinessCard(){
        Firebase businessRef = appRef.child(getString(R.string.child_business));

        Query businessQuery = businessRef.orderByChild(getString(R.string.child_business_this_admin))
                        .equalTo(appRef.getAuth().getUid()).limitToFirst(1);

        businessQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot currentSnapShot = dataSnapshot.getChildren().iterator().next();
                    businessId = currentSnapShot.getKey();
                    currentBusiness = currentSnapShot.getValue(Business.class);

                    TextView currentText = (TextView) findViewById(R.id.profile_business_name);
                    currentText.setText(currentBusiness.getName());
                    currentText = (TextView) findViewById(R.id.profile_business_description);
                    currentText.setText(currentBusiness.getDescription());
                    setupBusinessDetailsCard();
                    setupStatisticsCards();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

                    if (currentDetails.getSacNumber() != null &&
                            !currentDetails.getSacNumber().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group1);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_sac_phone);
                        currentText.setText(currentDetails.getSacNumber());
                    }

                    /**TODO: Must setup the images so they can send an intent for each app**/
                    if (currentDetails.getEmail() != null &&
                            !currentDetails.getEmail().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group2);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_email);
                        currentText.setText(currentDetails.getEmail());
                    }

                    if (currentDetails.getWebsite() != null &&
                            !currentDetails.getWebsite().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group3);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_website);
                        currentText.setText(currentDetails.getWebsite());
                    }

                    if (currentDetails.getWhatsapp() != null &&
                            !currentDetails.getWebsite().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group4);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_whatsapp);
                        currentText.setText(currentDetails.getWhatsapp());
                    }

                    if (currentDetails.getFacebookPage() != null &&
                            !currentDetails.getFacebookPage().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group5);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_facebook);
                        currentText.setText(currentDetails.getFacebookPage());
                    }

                    if (currentDetails.getInstagramPage() != null &&
                            !currentDetails.getInstagramPage().isEmpty()) {
                        sacAllEmptyFlag = false;
                        linearLayout = (LinearLayout) findViewById(R.id.profile_group6);
                        linearLayout.setVisibility(View.VISIBLE);
                        currentText = (TextView) findViewById(R.id.profile_business_instagram);
                        currentText.setText(currentDetails.getInstagramPage());
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

    public void goToEditBusinessInfo(View editButton){
        Intent intent = new Intent(this, EditBusinessInfoActivity.class);
        intent.putExtra("businessId", businessId);
        intent.putExtra(getString(R.string.child_business_this_name),
                                    currentBusiness.getName());
        intent.putExtra(getString(R.string.child_business_this_category),
                                    currentBusiness.getCategory());
        intent.putExtra(getString(R.string.child_business_this_description),
                                    currentBusiness.getDescription());
        intent.putExtra(getString(R.string.child_business_this_corporate_number),
                currentBusiness.getCorporateNumber());

        startActivity(intent);
    }

    public void goToEditContactInfo(View editButton){
        Intent intent = new Intent(this, EditContactInfoActivity.class);
        intent.putExtra("businessId", businessId);
        intent.putExtra(getString(R.string.child_details_this_email), currentDetails.getEmail());
        intent.putExtra(getString(R.string.child_details_this_website), currentDetails.getWebsite());
        intent.putExtra(getString(R.string.child_details_this_sac), currentDetails.getSacNumber());
        intent.putExtra(getString(R.string.child_details_this_whatsapp), currentDetails.getWhatsapp());
        intent.putExtra(getString(R.string.child_details_this_facebook), currentDetails.getFacebookPage());
        intent.putExtra(getString(R.string.child_details_this_instagram), currentDetails.getInstagramPage());
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
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void retrieveBusinessdata(){
        Firebase businessRef = appRef.child(getString(R.string.child_business));

        Query businessQuery = businessRef.orderByChild(getString(R.string.child_business_this_admin))
                .equalTo(appRef.getAuth().getUid());

        businessQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    businessId = dataSnapshot.getKey();
                    currentBusiness = dataSnapshot.getChildren().iterator().next().getValue(Business.class);
                    retrieveDetails();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void retrieveDetails(){
        Firebase detailsRef = appRef.child(getString(R.string.child_business_details));
        Query detailsQuery = detailsRef.orderByKey().equalTo(businessId);
        detailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentDetails = dataSnapshot.getChildren().iterator()
                            .next().getValue(BusinessDetails.class);
                    //retrieveStatistics();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void retrieveStatistics(){
        Firebase statisticsRef = appRef.child(getString(R.string.child_business_statistics));
        Query statisticsQuery = statisticsRef.orderByKey().equalTo(businessId);
        statisticsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentStatistics = dataSnapshot.getChildren().iterator()
                            .next().getValue(BusinessStatistics.class);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void saveUpdatesOnBusiness(View confirmationButton){
        Firebase currentBusinessRef = appRef.child(getString(R.string.child_business))
                .child(businessId);

        currentBusinessRef.updateChildren(updatesOnBusiness);

        Firebase currentDetailsRef = appRef.child(getString(R.string.child_business_details))
                .child(businessId);
        currentDetailsRef.updateChildren(updatesOnDetails);

    }

}
