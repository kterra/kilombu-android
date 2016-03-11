package kilombu.kilombuapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;

import kilombu.kilombuapp.models.Business;
import kilombu.kilombuapp.models.User;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private final String TAG = "MAIN";
    private Firebase appRef;
    private final int adsPerPage = 20;
    private final long placeholderRank = 999997;
    private int currentPage = 1;
    private int currentCategory; // 0 represents "All categories"
    private RecyclerView adsView;
    private User currentUser;
    private Business currentBusiness;
    private String currentBusinessId;
    private FirebaseAdsRecyclerAdapter firebaseAdsAdapter;
    private Firebase businessRef;
    private Query businessQuery;
    private Query getUser;
    private SharedPreferences busPreferences;
    private SharedPreferences userPreferences;
    private android.content.Context context;
    private DrawerLayout drawer;
    private LinearLayoutManager llm;

    private boolean isTransition = false;
    private boolean shouldUseLocation;
    private GeoFireAdsRecyclerAdapter geofireAdsAdapter;
    private float startRadius = 10.0f;
    private GeoLocation userQueryLocation;
    private RecyclerView.Adapter adsRecyclerAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private EditText addressEdit;
    private ProgressBar waitingForLocation;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 500; // 1000 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        context = MainActivity.this;
        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);


        llm = new LinearLayoutManager(this);
        adsView =(RecyclerView)findViewById(R.id.ads_recycler_view);
        adsView.setLayoutManager(llm);
        adsView.requestFocus();
        adsView.setHasFixedSize(true);

        initializeCategoryFloatingButtons();

        businessRef = new Firebase(getString(R.string.firebase_url))
                            .child(getString(R.string.child_business));

        appRef = new Firebase(getString(R.string.firebase_url));
        //TODO: deal with sample
        userQueryLocation = new GeoLocation(-22.924315, -43.238963);
        //userQueryLocation = new GeoLocation(2.8071985, -60.731581);
        initializeAdapter();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {
                setNavigationHeader();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        setUpCustomDrawer();
        //ValidationTools.createBusinessPlaceholders(this);
        //Utils.createBusinessLocation(this);
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        addressEdit = ((EditText) findViewById(R.id.address_selected));

        Log.d(TAG, "ON CREATE");


        buildGoogleApiClient();

    }



    private void setNavigationHeader(){
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        String userName = userPreferences.getString(getString(R.string.username_key),"");
        //Log.d(TAG, userName);
        ((TextView) findViewById(R.id.user_name_header)).setText(userName);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        String businessName = busPreferences.getString(getString(R.string.businessname_key),"");
        //Log.d(TAG, businessName);
        ((TextView) findViewById(R.id.user_mail_header)).setText(businessName);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        navigationView.getMenu().clear(); //clear old inflated items.
        String businessId = busPreferences.getString(getString(R.string.businessid_key),"");

        if(businessId.isEmpty()){
            if(userName.isEmpty()){
                navigationView.inflateMenu(R.menu.activity_main_drawer);
            }else{
                navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
            }

        }else{
            navigationView.inflateMenu(R.menu.activity_main_drawer_entrepreneur);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;

    }


    @Override
    protected void onResume() {
        super.onResume();
        Firebase.goOnline();
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        shouldUseLocation =  userPreferences.getBoolean(getString(R.string.shoulduselocation_key), false);
        if(shouldUseLocation){
            waitingForLocation = (ProgressBar) findViewById(R.id.progressBar);
            waitingForLocation.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
            waitingForLocation.setVisibility(View.VISIBLE);
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            mGoogleApiClient.connect();
        }

        Log.d(TAG, "ON RESUME");
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_black_24dp));
        }
        super.onStop();
        if (! isTransition){
            Firebase.goOffline();
            Log.d(TAG, "GOING OFFLINE");
        }else{
            Log.d(TAG, "TRANSITION");
        }
        Log.d(TAG, "ON STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ON DESTROY");
        if (firebaseAdsAdapter != null){
            firebaseAdsAdapter.cleanup();
        }

        if (geofireAdsAdapter != null){
            geofireAdsAdapter.cleanup();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpCustomDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (appRef.getAuth() != null) {
            String userId = appRef.getAuth().getUid();
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putString(getString(R.string.userid_key), userId);
            userEditor.commit();
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);

            //set nav header text

            getUser = appRef.child(getString(R.string.child_users)).orderByKey().equalTo(userId);
            getUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUser = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                        String userName = currentUser.getName();

                        SharedPreferences.Editor userEditor = userPreferences.edit();

                        userEditor.putString(getString(R.string.username_key), userName);
                        userEditor.putString(getString(R.string.useremail_key), currentUser.getEmail());
                        userEditor.commit();

                        ((TextView) findViewById(R.id.user_name_header)).setText(userName);


                    } else {
                        //TODO: what if user data is not found?
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            Query businessQuery = businessRef.orderByChild(getString(R.string.child_business_this_admin))
                    .equalTo(userId).limitToLast(1);

            businessQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot currentSnapShot = dataSnapshot.getChildren().iterator().next();

                        currentBusinessId = currentSnapShot.getKey();
                        currentBusiness = currentSnapShot.getValue(Business.class);

                        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
                        String currentBusinessAdmin = currentBusiness.getAdmin();
                        String currentBusinessName = currentBusiness.getName();
                        String currentBusinessDescription = currentBusiness.getDescription();
                        String currentBusinessCorpNumber = currentBusiness.getCorporateNumber();
                        String currentBusinessCategory = ValidationTools
                                .categoryForIndex(currentBusiness.getCategory(), MainActivity.this);


                        SharedPreferences.Editor busEditor = busPreferences.edit();
                        busEditor.putString(getString(R.string.businessid_key), currentBusinessId);
                        busEditor.putString(getString(R.string.businessadmin_key), currentBusinessAdmin);
                        busEditor.putString(getString(R.string.businessname_key), currentBusinessName);
                        busEditor.putString(getString(R.string.businessdescription_key), currentBusinessDescription);
                        busEditor.putString(getString(R.string.businesscorpnumber_key), currentBusinessCorpNumber);
                        busEditor.putString(getString(R.string.businesscategory_key), currentBusinessCategory);
                        busEditor.commit();

                        ((TextView) findViewById(R.id.user_mail_header)).setText(currentBusinessName);

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        navigationView.setNavigationItemSelectedListener(MainActivity.this);
                        navigationView.getMenu().clear(); //clear old inflated items.
                        navigationView.inflateMenu(R.menu.activity_main_drawer_entrepreneur);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }

    private void initializeAdapter(){
        final ProgressBar loadingArea = (ProgressBar) findViewById(R.id.progressBar);
        loadingArea.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));

        currentCategory = 0;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        shouldUseLocation =  userPreferences.getBoolean(getString(R.string.shoulduselocation_key), false);

        if (shouldUseLocation){
            GeoFire geoFire = new GeoFire(appRef.child(getString(R.string.child_business_geolocation))
                                                        .child(getString(R.string.category_all)));
            GeoQuery geoquery = geoFire.queryAtLocation(userQueryLocation, 10.0);

            geofireAdsAdapter = new GeoFireAdsRecyclerAdapter(geoquery, appRef.child(getString(R.string.child_business)));
            adsView.setAdapter(geofireAdsAdapter);
        }
        else{
            businessQuery = businessRef.orderByChild(getString(R.string.child_business_rankpoints))
                    .endAt(placeholderRank)
                    .limitToFirst(adsPerPage);
            firebaseAdsAdapter = new FirebaseAdsRecyclerAdapter(businessQuery);

            adsView.setAdapter(firebaseAdsAdapter);
        }
    }

    private void updateFirebaseAdapter(Query query){
        //if we are transitioning from geofireAdsAdapter
        if (geofireAdsAdapter != null){
            geofireAdsAdapter.cleanup();
            geofireAdsAdapter = null;
            firebaseAdsAdapter = new FirebaseAdsRecyclerAdapter(query);
            adsView.setAdapter(firebaseAdsAdapter);
        }else {
            //if we had an active firebaseAdsAdapter
            if (firebaseAdsAdapter != null) {
                firebaseAdsAdapter.cleanup();
                firebaseAdsAdapter = new FirebaseAdsRecyclerAdapter(query);
                adsView.swapAdapter(firebaseAdsAdapter, true);
            }
        }
    }

    private void updateGeofireAdapter(GeoQuery query, Firebase modelRef){
        if (firebaseAdsAdapter != null){
            firebaseAdsAdapter.cleanup();
            firebaseAdsAdapter = null;
            geofireAdsAdapter = new GeoFireAdsRecyclerAdapter(query, modelRef);
            adsView.setAdapter(geofireAdsAdapter);
        }else {
            if (geofireAdsAdapter != null) {
                geofireAdsAdapter.cleanup();
                geofireAdsAdapter = new GeoFireAdsRecyclerAdapter(query, modelRef);
                adsView.swapAdapter(geofireAdsAdapter, true);
            }
        }
    }

    public void changeCategoryOnClick(View button){
        String category = button.getTag().toString();
        changeCategory(category);
    }

    public void changeCategory(String category){
        currentPage = 1;

        if (category.equals(getString(R.string.category_select_all))){
            currentCategory = 0;
            category = getString(R.string.category_all);
            Log.d(TAG, "modified category: " + category);
        }
        else{
            currentCategory = ValidationTools.convertCategory(category, this);
        }

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        shouldUseLocation =  userPreferences.getBoolean(getString(R.string.shoulduselocation_key), false);
        //Log.d("changeCategoryOnClick", Integer.toString(currentCategory));
        if (shouldUseLocation){
            GeoFire geoRef = new GeoFire(appRef.child(getString(R.string.child_business_geolocation)).child(category));
            GeoQuery query = geoRef.queryAtLocation(userQueryLocation, startRadius);
            updateGeofireAdapter(query, appRef.child(getString(R.string.child_business)));

        }else{
            if (currentCategory != 0){
                int begin = currentCategory * Business.categoryOffset;
                int end = (currentCategory + 1) * Business.categoryOffset - 1;
                businessQuery = businessRef.orderByChild(
                        getString(R.string.child_business_category_rankpoints))
                        .startAt(begin)
                        .endAt(end)
                        .limitToFirst(adsPerPage);

            }else{
                businessQuery = businessRef.orderByChild(getString(R.string.child_business_rankpoints))
                        .endAt(placeholderRank).limitToFirst(adsPerPage);
            }

            updateFirebaseAdapter(businessQuery);
        }
    }

    public void showListWithAllCategories(View button){
        Intent intent = new Intent(MainActivity.this, CategoriesListActivity.class);
        isTransition = true;
        MainActivity.this.startActivityForResult(intent, 1);
    }

    private void initializeCategoryFloatingButtons(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.select_all);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);
            }
        });

        FloatingActionButton fabBeauty = (FloatingActionButton) findViewById(R.id.beauty_button);
        fabBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabArt = (FloatingActionButton) findViewById(R.id.culture_button);
        fabArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabEdu = (FloatingActionButton) findViewById(R.id.education_button);
        fabEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabSport = (FloatingActionButton) findViewById(R.id.sports_button);
        fabSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabFashion = (FloatingActionButton) findViewById(R.id.fashion_button);
        fabFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabPubli = (FloatingActionButton) findViewById(R.id.publi_button);
        fabPubli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabRH = (FloatingActionButton) findViewById(R.id.rh_button);
        fabRH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabHealth = (FloatingActionButton) findViewById(R.id.health_button);
        fabHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabSoftware = (FloatingActionButton) findViewById(R.id.software_button);
        fabSoftware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategoryOnClick(view);

            }
        });
        FloatingActionButton fabMore = (FloatingActionButton) findViewById(R.id.more_button);
        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListWithAllCategories(view);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    changeCategory(data.getStringExtra("result"));
                }
                break;
            case Utils.LOCATION_REQUEST:
                if (resultCode == Activity.RESULT_OK){
                    userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
                    SharedPreferences.Editor userEditor = userPreferences.edit();
                    userEditor.putBoolean(getString(R.string.shoulduselocation_key),true);
                    userEditor.commit();

                    LatLng latLng = (LatLng) data.getParcelableExtra("LatLng");
                    userQueryLocation = new GeoLocation(latLng.latitude, latLng.longitude);
                    Log.d(TAG, "current category: " + currentCategory);

                    String category = currentCategory == 0 ? getString(R.string.category_select_all) :
                                            ValidationTools.categoryForIndex(currentCategory, this);
                    Log.d(TAG, "computed category: " + category);
                    changeCategory(category);
                }
            case 5:
//                    if(!mGoogleApiClient.isConnected()){
//                        mGoogleApiClient.connect();
//                        waitingForLocation = (ProgressBar) findViewById(R.id.progressBar);
//                        waitingForLocation.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
//                        waitingForLocation.setVisibility(View.VISIBLE);
//
//                    }else{
//                        mGoogleApiClient.disconnect();
//                        ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_black_24dp));
//                        mGoogleApiClient.connect();
//                    }
                break;
            default:
                break;
        }
    }

    public void nextPage(View nextButton){
        Query nextPageQuery;
        try {
            Business lastItem = firebaseAdsAdapter.getItem(firebaseAdsAdapter.getItemCount() - 2);
            if (currentCategory == 0){
                nextPageQuery = new Firebase(getString(R.string.firebase_url))
                        .child(getString(R.string.child_business))
                        .orderByChild(getString(R.string.child_business_rankpoints))
                        .startAt(lastItem.getRankPoints())
                        .endAt(placeholderRank).limitToFirst(adsPerPage);
            }
            else{

                nextPageQuery = new Firebase(getString(R.string.firebase_url))
                        .child(getString(R.string.child_business))
                        .orderByChild(getString(R.string.child_business_category_rankpoints))
                        .startAt(lastItem.getCategoryRankPoints())
                        .endAt((currentCategory + 1) * Business.categoryOffset - 1)
                        .limitToFirst(adsPerPage);
            }

            updateFirebaseAdapter(nextPageQuery);
            currentPage += 1;
        }catch (IndexOutOfBoundsException e){
            //nothing to say to the user
        }

    }

    public void previousPage(View prevButton){
        Query previousPageQuery;
        try {
            Business firstItem = firebaseAdsAdapter.getItem(0);
            if (currentCategory == 0){
                previousPageQuery = new Firebase(getString(R.string.firebase_url))
                        .child(getString(R.string.child_business))
                        .orderByChild(getString(R.string.child_business_rankpoints))
                        .endAt(firstItem.getRankPoints()).limitToFirst(adsPerPage);
            }
            else{
                previousPageQuery = new Firebase(getString(R.string.firebase_url))
                        .child(getString(R.string.child_business))
                        .orderByChild(getString(R.string.child_business_category_rankpoints))
                        .startAt(currentCategory * Business.categoryOffset)
                        .endAt(firstItem.getCategoryRankPoints())
                        .limitToLast(adsPerPage);
            }

            updateFirebaseAdapter(previousPageQuery);
            currentPage -= 1;
        }catch (IndexOutOfBoundsException e){
            //nothing to say to the user
        }

    }
    public void filterLocation(View button){
        Intent intent = new Intent(this, FilterLocationActivity.class);
        isTransition = true;
        startActivityForResult(intent, Utils.LOCATION_REQUEST);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }



    @Override
    public void onConnected(Bundle connectionHint) {

        ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_green_24dp));

        try{
            waitingForLocation = (ProgressBar) findViewById(R.id.progressBar);
            waitingForLocation.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
            waitingForLocation.setVisibility(View.GONE);
            mLocationRequest = LocationRequest.create();
           //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
           mLocationRequest.setInterval(UPDATE_INTERVAL);
           mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
           LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }catch (SecurityException se){
            Toast.makeText(this, R.string.action_forbbiden, Toast.LENGTH_SHORT).show();
        }

        }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        shouldUseLocation = false;
        String category = ValidationTools.categoryForIndex(currentCategory, this);

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.putBoolean(getString(R.string.shoulduselocation_key), false);
        userEditor.commit();

        changeCategory(category);
    }

    @Override
    public void onLocationChanged(Location location) {
        try{

          //  Toast.makeText(this, "Location " + location.getLatitude()+ ", "+ location.getLongitude(), Toast.LENGTH_SHORT).show();


            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean(getString(R.string.shoulduselocation_key),true);
            userEditor.commit();

            userQueryLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            String category = ValidationTools.categoryForIndex(currentCategory, this);
            changeCategory(category);
        }catch (NullPointerException nulle){

        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_signin) {
            Intent intent = new Intent(this, LoginActivity.class);
            isTransition = true;
            startActivity(intent);
        } else if (id == R.id.menu_signup) {
            Intent intent = new Intent(this, SignUpActivity.class);
            isTransition = true;
            startActivity(intent);

        } else if (id == R.id.signout_menu) {
            appRef.unauth();
            
            busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key),
                                                            android.content.Context.MODE_PRIVATE);
            busPreferences.edit().clear().commit();
            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key),
                                                            android.content.Context.MODE_PRIVATE);
            userPreferences.edit().clear().commit();

            Intent intent = new Intent(this, MainActivity.class);
            isTransition = true;
            finish();
            startActivity(intent);

        } else if (id == R.id.business_create_menu) {
            Intent intent = new Intent(this, CreateBusinessActivity.class);
            isTransition = true;
            startActivity(intent);

        } else if (id == R.id.business_menu) {
            Intent intent = new Intent(this, BusinessProfileActivity.class);
            isTransition = true;
            startActivity(intent);

        } else if (id == R.id.settings_menu) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            //finish();
            isTransition = true;
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView businessName;
        TextView shortDescription;

        public BusinessViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            businessName = (TextView)itemView.findViewById(R.id.business_name);
            shortDescription = (TextView)itemView.findViewById(R.id.business_description);

        }
    }

    class FirebaseAdsRecyclerAdapter extends FirebaseRecyclerAdapter<Business, RecyclerView.ViewHolder>{

        private class VIEW_TYPES {
            public static final int Header = 1;
            public static final int Normal = 2;
            public static final int Footer = 3;
        }

        ProgressBar loadingArea;
        FooterViewHolder footerViewHolder;


        public FirebaseAdsRecyclerAdapter(Class<Business> modelClass, int modelLayout,
                                         Class<RecyclerView.ViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);

        }

        public FirebaseAdsRecyclerAdapter(Query ref){
            super(Business.class, R.layout.item, RecyclerView.ViewHolder.class, ref);
            loadingArea = (ProgressBar) findViewById(R.id.progressBar);
            loadingArea.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount()-1){
                return VIEW_TYPES.Footer;
            }
            return VIEW_TYPES.Normal;
        }

        @Override
        public int getItemCount() {
            //must account footer
            return (super.getItemCount()+1);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType){
                case VIEW_TYPES.Footer:
                    ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.footer, parent, false);
                    return new FooterViewHolder(view);
                default:
                    ViewGroup mview = (ViewGroup) LayoutInflater.from(parent.getContext())
                                    .inflate(mModelLayout, parent, false);
                    return new BusinessViewHolder(mview);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            Business model = null;
            if (position < getItemCount()-1){
                model = this.getItem(position);
            }
            this.populateViewHolder(viewHolder, model, position);
        }

        @Override
        protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, final Business business, final int position) {

            if (business == null){ //footer
                //Log.d("FOOTER POS", Integer.toString(position));
                int footerPosition = getItemCount() -1;
                //Log.d("FOOTER ITENS", Integer.toString(getItemCount()));

                footerViewHolder = (FooterViewHolder) viewHolder;
                if (currentPage == 1){
                    footerViewHolder.navigateBackLayout.setVisibility(View.GONE);
                }else{
                    footerViewHolder.navigateBackLayout.setVisibility(View.VISIBLE);
                }

                footerViewHolder.navigateNextLayout.setVisibility(View.VISIBLE);

            }
            else {

                String businessName = business.getName();

                BusinessViewHolder businessViewHolder = (BusinessViewHolder) viewHolder;
                businessViewHolder.businessName.setText(businessName);
                String description = business.getDescription();
                if(description!= null && description.length() > 300){

                    description = description.substring(0,299);
                    description = description + " ...";

                }

                businessViewHolder.shortDescription.setText(description);

                if(businessName.equals(getString(R.string.no_ads_left))){

                    CardView cv = businessViewHolder.cv;

                    cv.setVisibility(View.VISIBLE);
                    RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                    LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                    childView.findViewById(R.id.cardview_arrow).setVisibility(View.GONE);
                    ((TextView)childView.findViewById(R.id.business_description)).setTextSize(12);


                    TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                    businessNameView.setTextSize(16);

                    View view =  (View) relativeLayoutView.getChildAt(1);
                    view.setVisibility(View.GONE);

                    if (footerViewHolder.navigateNextLayout != null){
                        footerViewHolder.navigateNextLayout.setVisibility(View.GONE);
                    }


                }else{
                    CardView cv = businessViewHolder.cv;
                    cv.setVisibility(View.VISIBLE);
                    //cv.requestFocus();
                    RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                    LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                    childView.findViewById(R.id.cardview_arrow).setVisibility(View.VISIBLE);
                    ((TextView)childView.findViewById(R.id.business_description)).setTextSize(16);


                    TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                    businessNameView.setTextSize(20);

                    View view =  (View) relativeLayoutView.getChildAt(1);
                    view.setVisibility(View.VISIBLE);

                }


                businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("NOME", business.getName());
                        if (business.getName().equals(getString(R.string.no_ads_left))) {
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, BusinessDetailsActivity.class);

                        intent.putExtra("business_name", business.getName());
                        intent.putExtra("business_category", business.getCategory());
                        intent.putExtra("business_description", business.getDescription());

                        Firebase itemRef = firebaseAdsAdapter.getRef(position);
                        String itemKey = itemRef.getKey();
                        intent.putExtra("businessId", itemKey);
                        isTransition = true;
                        MainActivity.this.startActivity(intent);

                    }
                });

                if (position > 0 || position == getItemCount() - 2) {
                    loadingArea.setVisibility(View.GONE);

                }

            }

        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat navigateBackLayout;
        private LinearLayoutCompat navigateNextLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            navigateBackLayout = (LinearLayoutCompat) itemView.findViewById(R.id.navigate_back_layout);
            navigateNextLayout = (LinearLayoutCompat) itemView.findViewById(R.id.navigate_next_layout);
        }
    }

    class GeoFireAdsRecyclerAdapter extends GeoFireRecyclerAdapter<Business, BusinessViewHolder> {

        ProgressBar loadingArea;


        public GeoFireAdsRecyclerAdapter(Class<Business> modelClass, int modelLayout,
                                          Class<BusinessViewHolder> viewHolderClass, GeoQuery georef, Firebase ref) {
            super(modelClass, modelLayout, viewHolderClass, georef, ref);

        }

        public GeoFireAdsRecyclerAdapter(GeoQuery georef, Firebase ref){
            super(Business.class, R.layout.item, BusinessViewHolder.class, georef, ref);
            loadingArea = (ProgressBar) findViewById(R.id.progressBar);
            loadingArea.setVisibility(View.VISIBLE);
        }

        @Override
        protected void populateViewHolder(BusinessViewHolder viewHolder, final Business business, final int position) {

            String businessName = business.getName();

            BusinessViewHolder businessViewHolder = (BusinessViewHolder) viewHolder;
            businessViewHolder.businessName.setText(businessName);
            String description = business.getDescription();
            if(description!= null && description.length() > 300){

                description = description.substring(0,299);
                description = description + " ...";

            }

            businessViewHolder.shortDescription.setText(description);

           if(businessName.equals(getString(R.string.no_ads_left)) || businessName.equals(getString(R.string.no_ads_here))){

                CardView cv = businessViewHolder.cv;

                cv.setVisibility(View.VISIBLE);
                RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                childView.findViewById(R.id.cardview_arrow).setVisibility(View.GONE);
                ((TextView)childView.findViewById(R.id.business_description)).setTextSize(12);


                TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                businessNameView.setTextSize(16);

                View view =  (View) relativeLayoutView.getChildAt(1);
                view.setVisibility(View.GONE);


            }
           else{
                CardView cv = businessViewHolder.cv;
                cv.setVisibility(View.VISIBLE);
                cv.requestFocus();
                RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                childView.findViewById(R.id.cardview_arrow).setVisibility(View.VISIBLE);
                ((TextView)childView.findViewById(R.id.business_description)).setTextSize(16);


                TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                businessNameView.setTextSize(20);

                View view =  (View) relativeLayoutView.getChildAt(1);
                view.setVisibility(View.VISIBLE);
           }


            businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("NOME", business.getName());
                    if (business.getName().equals(getString(R.string.no_ads_left))
                            || business.getName().equals(getString(R.string.no_ads_here))) {
                        return;
                    }
                    Intent intent = new Intent(MainActivity.this, BusinessDetailsActivity.class);

                    intent.putExtra("business_name", business.getName());
                    intent.putExtra("business_category", business.getCategory());
                    intent.putExtra("business_description", business.getDescription());

                    Firebase itemRef = geofireAdsAdapter.getRef(position);
                    String itemKey = itemRef.getKey();
                    intent.putExtra("businessId", itemKey);
                    isTransition = true;
                    MainActivity.this.startActivity(intent);
                }
            });

            //maybe I can do this with a listener and put this class in another file
            if (position >= 0 || position == getItemCount() - 2) {
                loadingArea.setVisibility(View.GONE);
            }
        }
    }

    public void getLatLongFromAddress(View button){

        try{
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
            }

            ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_black_24dp));

            String address = addressEdit.getText().toString().trim();
            LatLng latLng = Utils.getLocationFromAddress(context, address);

            findViewById(R.id.clear).setVisibility(View.VISIBLE);
            findViewById(R.id.get).setVisibility(View.INVISIBLE);

            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean(getString(R.string.shoulduselocation_key), true);
            userEditor.commit();


            userQueryLocation = new GeoLocation(latLng.latitude, latLng.longitude);
            String category = ValidationTools.categoryForIndex(currentCategory, this);
            changeCategory(category);
        }
        catch (NullPointerException nulle){
            //nothing to do here :)
        }
    }


    public void getAllLocations(View button){

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

        ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_black_24dp));

        findViewById(R.id.clear).setVisibility(View.INVISIBLE);
        findViewById(R.id.get).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.address_selected)).setText("");
        shouldUseLocation = false;
        String category = ValidationTools.categoryForIndex(currentCategory, this);

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.putBoolean(getString(R.string.gpscontrol_key), false);
        userEditor.putBoolean(getString(R.string.shoulduselocation_key), false);
        userEditor.commit();
        changeCategory(category);
    }

    public void GPSControl(View button)
    {
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        Boolean gpsControl = userPreferences.getBoolean(getString(R.string.gpscontrol_key), false);


        if(!gpsControl){
            final LocationManager manager = (LocationManager) getSystemService( this.LOCATION_SERVICE );


            if(!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER ))) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 5);
            }

            mGoogleApiClient.connect();

            findViewById(R.id.clear).setVisibility(View.INVISIBLE);
            findViewById(R.id.get).setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.address_selected)).setText("");

            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean(getString(R.string.gpscontrol_key), true);
            userEditor.putBoolean(getString(R.string.shoulduselocation_key), true);
            userEditor.commit();

            waitingForLocation = (ProgressBar) findViewById(R.id.progressBar);
            waitingForLocation.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x7f7f7f));
            waitingForLocation.setVisibility(View.VISIBLE);

        }else{
            mGoogleApiClient.disconnect();

            shouldUseLocation = false;
            String category = ValidationTools.categoryForIndex(currentCategory, this);

            ((ImageView) findViewById(R.id.gpsControlButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_my_location_black_24dp));


            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean(getString(R.string.gpscontrol_key), false);
            userEditor.putBoolean(getString(R.string.shoulduselocation_key), false);
            userEditor.commit();


            changeCategory(category);
        }

    }
}
