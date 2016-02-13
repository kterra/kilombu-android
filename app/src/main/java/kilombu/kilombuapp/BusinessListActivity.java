package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class BusinessListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Firebase businessRef;
    private RecyclerView adsView;
    private FirebaseRecyclerAdapter<Business, BusinessViewHolder> firebaseAdsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        businessRef = new Firebase(getString(R.string.firebase_url)).child("business");

        adsView =(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        adsView.setLayoutManager(llm);
        adsView.setHasFixedSize(true);

        initializeAdapter();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initializeAdapter(){
        Query businessQuery = businessRef.orderByKey().limitToFirst(15);
        firebaseAdsAdapter = new FirebaseRecyclerAdapter<Business, BusinessViewHolder>(Business.class,
                R.layout.item, BusinessViewHolder.class, businessQuery) {
            @Override
            protected void populateViewHolder(BusinessViewHolder businessViewHolder, final Business business, final int position) {
                businessViewHolder.businessName.setText(business.getName());
                businessViewHolder.businessCategory.setText(business.getCategory());
                businessViewHolder.shortDescription.setText(business.getDescription());

                businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BusinessListActivity.this, BusinessDetailsActivity.class);

                        intent.putExtra("business_name", business.getName());
                        intent.putExtra("business_category", business.getCategory());
                        intent.putExtra("business_description", business.getDescription());

                        Firebase itemRef = firebaseAdsAdapter.getRef(position);
                        String itemKey = itemRef.getKey();
                        intent.putExtra("business_key", itemKey);

                        BusinessListActivity.this.startActivity(intent);

                    }
                });
            }
        };
        adsView.setAdapter(firebaseAdsAdapter);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_options) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_sign_up){
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_create_business){
            Intent intent = new Intent(this, CreateBusinessActivity.class);
            startActivity(intent);
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView businessName;
        TextView businessCategory;
        TextView shortDescription;

        public BusinessViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            businessName = (TextView)itemView.findViewById(R.id.business_name);
            businessCategory = (TextView)itemView.findViewById(R.id.business_category);
            shortDescription = (TextView)itemView.findViewById(R.id.business_description);

        }
    }

}
