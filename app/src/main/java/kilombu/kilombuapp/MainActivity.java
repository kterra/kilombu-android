package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Firebase appRef;
    private final int adsPerPage = 30;
    private String currentCategory;
    private RecyclerView adsView;
    User currentUser;
    private FirebaseRecyclerAdapter<Business, BusinessViewHolder> firebaseAdsAdapter;
    private Firebase businessRef;
    private Query businessQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        adsView =(RecyclerView)findViewById(R.id.rv);
        adsView.setLayoutManager(llm);
        adsView.setHasFixedSize(true);

        currentCategory = getString(R.string.category_all);
        businessRef = new Firebase(getString(R.string.firebase_url)).child("business");
        initializeAdapter();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        appRef = new Firebase(getString(R.string.firebase_url));
        setUpCustomDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAdsAdapter.cleanup();
    }

    private void setUpCustomDrawer(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if( appRef.getAuth() != null){
            String userId = appRef.getAuth().getUid();
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);

            //set nav header text
            Query getUser = appRef.child("users").orderByKey().equalTo(userId);
            getUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d("auth", Long.toString(dataSnapshot.getChildrenCount()));
                                currentUser = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                                TextView username = (TextView) findViewById(R.id.user_name_header);
                                username.setText(currentUser.getName());
                                TextView usermail = (TextView) findViewById(R.id.user_mail_header);
                                usermail.setText(currentUser.getEmail());
                            }else{
                                //TODO: what if user data is not found?
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    }
            );
        }
    }

    private void initializeAdapter(){
        businessQuery = businessRef.orderByKey().limitToFirst(adsPerPage);
        firebaseAdsAdapter = new FirebaseRecyclerAdapter<Business, BusinessViewHolder>(Business.class,
                R.layout.item, BusinessViewHolder.class, businessQuery) {
            @Override
            protected void populateViewHolder(BusinessViewHolder businessViewHolder, final Business business, final int position) {
                businessViewHolder.businessName.setText(business.getName());
                businessViewHolder.shortDescription.setText(business.getDescription());

                businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
            }
        };
        adsView.setAdapter(firebaseAdsAdapter);
    }


    //TODO: consider rewriting own adapter
    public void changeCategory(View button){

        currentCategory = button.getTag().toString();
        Log.d("MAIN", currentCategory);
        if (currentCategory != getString(R.string.category_all)){
            businessQuery = businessRef.orderByChild("category").equalTo(currentCategory).limitToFirst(adsPerPage);
        }else{
            businessQuery = businessRef.orderByKey().limitToFirst(adsPerPage);
        }
        firebaseAdsAdapter = new FirebaseRecyclerAdapter<Business, BusinessViewHolder>(Business.class,
                R.layout.item, BusinessViewHolder.class, businessQuery) {
            @Override
            protected void populateViewHolder(BusinessViewHolder businessViewHolder, final Business business, final int position) {
                businessViewHolder.businessName.setText(business.getName());
                businessViewHolder.shortDescription.setText(business.getDescription());

                businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
            }
        };
        adsView.swapAdapter(firebaseAdsAdapter, true);
    }

    public void showListWithAllCategories(View button){
        Intent intent = new Intent(MainActivity.this, CategoriesListActivity.class);
        MainActivity.this.startActivity(intent);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.business_create_menu) {
            Intent intent = new Intent(this, CreateBusinessActivity.class);
            startActivity(intent);

        } else if (id == R.id.business_menu) {
            Intent intent = new Intent(this, BusinessProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.settings_menu) {
            Intent intent = new Intent(this, UserProfileActivity.class);
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
}
