package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<Business> businesses;
    private RecyclerView adsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        adsView =(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        adsView.setLayoutManager(llm);
        adsView.setHasFixedSize(true);

        initializeData();
        initializeAdapter();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeData(){
        businesses = new ArrayList<>();

        businesses.add(new Business(getString(R.string.nome1), null, "098687333/78",
                getString(R.string.cat1), getString(R.string.descricao1), "Rio de janeiro",
                getString(R.string.end1), "Segunda a Sexta: 08:00 - 12:00", "(21) 23456-7890",
                "lore_ipsum@qmail.com", null, null));
        businesses.add(new Business(getString(R.string.nome2), null, "098687333/78",
                getString(R.string.cat2), getString(R.string.descricao2), "Rio de janeiro",
                getString(R.string.end2), "Segunda a Sexta: 08:00 - 12:00", "(21) 23456-7890",
                "lore_ipsum@qmail.com", null, null));
        businesses.add(new Business(getString(R.string.nome3), null, "098687333/78",
                getString(R.string.cat3), getString(R.string.descricao3), "Rio de janeiro",
                getString(R.string.end3), "Segunda a Sexta: 08:00 - 12:00", "(21) 23456-7890",
                "lore_ipsum@qmail.com", null, null));
        businesses.add(new Business(getString(R.string.nome4), null, "098687333/78",
                getString(R.string.cat4), getString(R.string.descricao4), "Rio de janeiro",
                getString(R.string.end4), "Segunda a Sexta: 08:00 - 12:00", "(21) 23456-7890",
                "lore_ipsum@qmail.com", null, null));
        businesses.add(new Business(getString(R.string.nome5), null, "098687333/78",
                getString(R.string.cat5), getString(R.string.descricao5), "Rio de janeiro",
                getString(R.string.end5), "Segunda a Sexta: 08:00 - 12:00", "(21) 23456-7890",
                "lore_ipsum@qmail.com", null, null));
    }

    private void initializeAdapter(){
        AdsAdapter adapter = new AdsAdapter(this, businesses);
        adsView.setAdapter(adapter);
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
}
