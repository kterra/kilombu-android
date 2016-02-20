package kilombu.kilombuapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Firebase appRef;
    private final int adsPerPage = 2;
    private final int minViewableAds = 2;
    private int currentPage = 1;
    private String currentCategory;
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
    private boolean noAdsLeftFlag;


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


        LinearLayoutManager llm = new LinearLayoutManager(this);
        adsView =(RecyclerView)findViewById(R.id.ads_recycler_view);
        adsView.setLayoutManager(llm);
        adsView.setHasFixedSize(true);


        businessRef = new Firebase(getString(R.string.firebase_url))
                            .child(getString(R.string.child_business));


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


        appRef = new Firebase(getString(R.string.firebase_url));
        setUpCustomDrawer();
        Log.d("MAIN", "ON CREATE");


    }

    private void setNavigationHeader(){
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        String userName = userPreferences.getString(getString(R.string.username_key),"");
        Log.d("MAIN", userName);
        ((TextView) findViewById(R.id.user_name_header)).setText(userName);

        busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
        String businessName = busPreferences.getString(getString(R.string.businessname_key),"");
        Log.d("MAIN", businessName);
        ((TextView) findViewById(R.id.user_mail_header)).setText(businessName);
    }
    @Override
    protected void onStart() {

        super.onStart();
        Log.d("MAIN", "ON START");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MAIN", "ON DESTROY");
        firebaseAdsAdapter.cleanup();
    }

    private void setUpCustomDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (appRef.getAuth() != null) {
            String userId = appRef.getAuth().getUid();
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putString(getString(R.string.userid_key),userId);
            userEditor.commit();
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);

            //set nav header text

            getUser = appRef.child("users").orderByKey().equalTo(userId);
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

                        String currentBusinessAdmin = currentBusiness.getAdmin();
                        String currentBusinessName = currentBusiness.getName();
                        String currentBusinessDescription = currentBusiness.getDescription();
                        String currentBusinessCorpNumber = currentBusiness.getCorporateNumber();
                        String currentBusinessCategory = currentBusiness.getCategory();


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

        currentCategory = getString(R.string.category_all);
        businessQuery = businessRef.orderByChild(getString(R.string.child_business_rankpoints))
                .limitToFirst(adsPerPage);
        firebaseAdsAdapter = new FirebaseAdsRecyclerAdapter(businessQuery);

        adsView.setAdapter(firebaseAdsAdapter);
    }

    private void updateFirebaseAdapter(Query query){
        firebaseAdsAdapter.cleanup();
        firebaseAdsAdapter = new FirebaseAdsRecyclerAdapter(query);
        adsView.swapAdapter(firebaseAdsAdapter, true);

    }

    //TODO: consider rewriting own adapter
    public void changeCategoryOnClick(View button){

        currentCategory = button.getTag().toString();
        currentPage = 1;
        Log.d("MAIN", currentCategory);
        if (currentCategory != getString(R.string.category_all)){
            businessQuery = businessRef.orderByChild(
                    getString(R.string.child_business_category_rankpoints))
                    .startAt(currentCategory).endAt(currentCategory + "\uF8FF")
                    .limitToLast(adsPerPage);
        }else{
            businessQuery = businessRef.orderByChild(getString(R.string.child_business_category_rankpoints))
                    .limitToLast(adsPerPage);
        }

        updateFirebaseAdapter(businessQuery);
    }

    public void changeCategory(String category){
        currentCategory = category;
        Log.d("MAIN", currentCategory);
        if (currentCategory != getString(R.string.category_all)){
            businessQuery = businessRef.orderByChild(
                    getString(R.string.child_business_this_category))
                    .equalTo(currentCategory).limitToLast(adsPerPage);
        }else{
            businessQuery = businessRef.orderByChild(getString(R.string.child_business_category_rankpoints))
                    .limitToLast(adsPerPage);
        }

        updateFirebaseAdapter(businessQuery);
    }

    public void showListWithAllCategories(View button){
        Intent intent = new Intent(MainActivity.this, CategoriesListActivity.class);
        MainActivity.this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                changeCategory(data.getStringExtra("result"));
            }
        }
    }

    public void nextPage(View nextButton){
        Query nextPageQuery;
        Business lastItem = firebaseAdsAdapter.getItem(firebaseAdsAdapter.getItemCount() - 2);
        //TODO: caso em que lastItem é null
        Log.d("ITEM RANK", Double.toString(lastItem.getRankPoints()));
        if (currentCategory.equals(getString(R.string.category_all))){
            nextPageQuery = new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business))
                    .orderByChild(getString(R.string.child_business_rankpoints))
                    .startAt(lastItem.getRankPoints()).limitToFirst(adsPerPage);
        }
        else{
            Log.d("ITEM CATRANK", Double.toString(lastItem.getRankPoints()));
            nextPageQuery = new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business))
                    .orderByChild(getString(R.string.child_business_category_rankpoints))
                    .startAt(lastItem.getCategoryRankPoints()).endAt(currentCategory + "\uF8FF")
                    .limitToFirst(adsPerPage);
        }

        updateFirebaseAdapter(nextPageQuery);
        currentPage += 1;

    }

    public void previousPage(View prevButton){
        Query previousPageQuery;
        Business firstItem = firebaseAdsAdapter.getItem(0);
        //TODO: caso em que firstItem é null
        Log.d("ITEM RANK", Double.toString(firstItem.getRankPoints()));
        if (currentCategory.equals(getString(R.string.category_all))){
            previousPageQuery = new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business))
                    .orderByChild(getString(R.string.child_business_rankpoints))
                    .endAt(firstItem.getRankPoints()).limitToFirst(adsPerPage);
        }
        else{
            previousPageQuery = new Firebase(getString(R.string.firebase_url))
                    .child(getString(R.string.child_business))
                    .orderByChild(getString(R.string.child_business_this_category))
                    .startAt(currentCategory + "\uF8FF").endAt(firstItem.getCategoryRankPoints())
                    .limitToFirst(adsPerPage);
        }

        updateFirebaseAdapter(previousPageQuery);
        currentPage -= 1;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_signin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_signup) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);

        } else if (id == R.id.signout_menu) {
            appRef.unauth();
            
            busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), android.content.Context.MODE_PRIVATE);
            busPreferences.edit().clear().commit();
            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
            userPreferences.edit().clear().commit();

            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.business_create_menu) {
            Intent intent = new Intent(this, CreateBusinessActivity.class);
            startActivity(intent);

        } else if (id == R.id.business_menu) {
            Intent intent = new Intent(this, BusinessProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.settings_menu) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            finish();
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
                Log.d("FOOTER POS", Integer.toString(position));
                int footerPosition = getItemCount() -1;
                Log.d("FOOTER ITENS", Integer.toString(getItemCount()));

                footerViewHolder = (FooterViewHolder) viewHolder;
                if (currentPage == 1){
                    footerViewHolder.navigateBackLayout.setVisibility(View.GONE);
                }else{
                    footerViewHolder.navigateBackLayout.setVisibility(View.VISIBLE);
                }

                footerViewHolder.navigateNextLayout.setVisibility(View.VISIBLE);

            }
            else {
                noAdsLeftFlag = false;
                String businessName = business.getName();

                BusinessViewHolder businessViewHolder = (BusinessViewHolder) viewHolder;
                businessViewHolder.businessName.setText(businessName);
                businessViewHolder.shortDescription.setText(business.getDescription());

                if(businessName.compareTo(getString(R.string.no_ads_left).trim())==0){

                    CardView cv = businessViewHolder.cv;

                    if(!noAdsLeftFlag){
                        cv.setVisibility(View.VISIBLE);
                        RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                        LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                        childView.findViewById(R.id.cardview_arrow).setVisibility(View.GONE);
                        ((TextView)childView.findViewById(R.id.business_description)).setTextSize(12);


                        TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                        businessNameView.setTextSize(16);

                        View view =  (View) relativeLayoutView.getChildAt(1);
                        view.setVisibility(View.GONE);

                        footerViewHolder.navigateNextLayout.setVisibility(View.GONE);
                        noAdsLeftFlag = true;

                    }else{
                        cv.setVisibility(View.GONE);
                    }



                }else{
                    CardView cv = businessViewHolder.cv;
                    cv.setVisibility(View.VISIBLE);
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
                        if (business.getName().equals(getString(R.string.no_ads_left))){
                            Log.d("IF", "RETORNA");
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, BusinessDetailsActivity.class);

                        intent.putExtra("business_name", business.getName());
                        intent.putExtra("business_category", business.getCategory());
                        intent.putExtra("business_description", business.getDescription());

                        Firebase itemRef = firebaseAdsAdapter.getRef(position);
                        String itemKey = itemRef.getKey();
                        intent.putExtra("businessId", itemKey);

                        MainActivity.this.startActivity(intent);

                    }
                });

                Log.d("POSITION", Integer.toString(position));
                Log.d("COUNTS", Integer.toString(getItemCount()));
                if (position > 0 || position == getItemCount() - 2) {
                    Log.d("MAIN", "TEM ANUNCIOS");
                    loadingArea.setVisibility(View.GONE);
                    //noAdsMessage.setVisibility(View.GONE);
                    //noAdsImage.setVisibility(View.GONE);
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


}
