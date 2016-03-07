package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class EditUserNameActivity extends AppCompatActivity {

    private  String id, name;
    private  EditText usernameProfile;
    private TextInputLayout inputLayoutName;
    private SharedPreferences userPreferences;
    private android.content.Context context;
    private boolean isTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = EditUserNameActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        name = userPreferences.getString(getString(R.string.username_key),"");
        id = userPreferences.getString(getString(R.string.userid_key), "");


        usernameProfile = (EditText) findViewById(R.id.edit_username);
        usernameProfile.setText(name);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName();

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


    private void changeUserName(){

        String newUserName =  usernameProfile.getText().toString().trim();

        if (!validateName(newUserName)) {
            return;
        }


        Map<String, Object> name = new HashMap<String, Object>();
        name.put(getString(R.string.child_business_this_name), newUserName);
        Firebase userRef = new Firebase(getString(R.string.firebase_url)).child(getString(R.string.child_users));
        Log.d("id", id);
        Log.d("name", newUserName);
        userRef.child(id).updateChildren(name);

        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.putString(getString(R.string.username_key), newUserName);
        userEditor.commit();

        Intent intent = new Intent(EditUserNameActivity.this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        isTransition = true;
        startActivity(intent);

    }

    private boolean validateName(String name) {
        inputLayoutName = (TextInputLayout) findViewById(R.id.username_edit_layout);

        if (!ValidationTools.isValidName(name)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus((EditText) findViewById(R.id.edit_username));
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
