package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditUserLocationActivity extends AppCompatActivity {

    private  String id, city, state;
    private EditText cityProfile;
    private TextInputLayout inputLayoutCity;
    private SharedPreferences userPreferences;
    private android.content.Context context;
    private boolean isTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_user_location));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = EditUserLocationActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        city = userPreferences.getString(getString(R.string.usercity_key), "");
        state = userPreferences.getString(getString(R.string.userstate_key), "");
        id = userPreferences.getString(getString(R.string.userid_key), "");


        cityProfile = (EditText) findViewById(R.id.edit_city);
        cityProfile.setText(city);

        Spinner stateSelection = (Spinner) findViewById(R.id.edit_state);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        stateSelection.setAdapter(adapter);
        ArrayList<String> states = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.states)));
        stateSelection.setSelection(states.indexOf(state));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndUpdateData();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d("MAIN", "ON START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (! isTransition){
            Firebase.goOffline();
            Log.d("MAIN", "GOING OFFLINE");
        }else{
            Log.d("MAIN", "TRANSITION");
        }
        Log.d("MAIN", "ON STOP");
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        isTransition = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void saveAndUpdateData() {
        String newUserCity=  cityProfile.getText().toString().trim();
        if(!validateCity(newUserCity)){
            return;
        }
        Firebase userRef = new Firebase(getString(R.string.firebase_url)).child(getString(R.string.child_users));
        Log.d("id", id);
        Log.d("city", newUserCity);
        userRef.child(id).child(getString(R.string.child_user_city)).setValue(newUserCity);

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.putString(getString(R.string.usercity_key), newUserCity);
        userEditor.commit();



        Spinner stateSelection = (Spinner) findViewById(R.id.edit_state);
        String newUserState = stateSelection.getSelectedItem().toString().trim();
        if(!validateState(newUserState, newUserCity)){
            return;
        }

        Log.d("id", id);
        Log.d("state", newUserState);
        userRef.child(id).child(getString(R.string.child_user_state)).setValue(newUserState);

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        userEditor = userPreferences.edit();
        userEditor.putString(getString(R.string.userstate_key), newUserState);
        userEditor.commit();

        Intent intent = new Intent(EditUserLocationActivity.this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        isTransition = true;
        startActivity(intent);

    }

    private boolean validateState(String state, String city){
        if (state.equals(getString(R.string.prompt_uf)) &&  ! city.isEmpty()){
            Toast.makeText(EditUserLocationActivity.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateCity(String city){
        inputLayoutCity = (TextInputLayout) findViewById(R.id.edit_city_layout);
        inputLayoutCity.setErrorEnabled(false);
        return true;
    }



}
