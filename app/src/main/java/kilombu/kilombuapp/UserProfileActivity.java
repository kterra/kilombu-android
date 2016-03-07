package kilombu.kilombuapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class UserProfileActivity extends AppCompatActivity {


    TextView usernameProfile, emailProfile, locationProfile;
    private SharedPreferences userPreferences;
    private android.content.Context context;
    private boolean isTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_user_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        context = UserProfileActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        String userName = userPreferences.getString(getString(R.string.username_key), "");
        String userEmail = userPreferences.getString(getString(R.string.useremail_key), "");
        String userCity = userPreferences.getString(getString(R.string.usercity_key),"" );
        String userState = userPreferences.getString(getString(R.string.userstate_key), "");

        usernameProfile = (TextView) findViewById(R.id.user_name_profile);
        usernameProfile.setText(userName);
        emailProfile = (TextView) findViewById(R.id.email_profile);
        emailProfile.setText(userEmail);
        locationProfile = (TextView) findViewById(R.id.location_profile);
        if (!userCity.trim().isEmpty()){
            locationProfile.setText(userCity +", "+ userState);
        }else{
            locationProfile.setText(getString(R.string.no_location_message));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d("MAIN", "ON START");
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

    public void changeName(View button){
        Intent intent = new Intent(UserProfileActivity.this, EditUserNameActivity.class);
        isTransition = true;
        startActivity(intent);
    }

    public void changeUserPassword(View confirmationButton){

        Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
        isTransition = true;
        startActivity(intent);
    }

    public void changeUserEmail(View confirmationButton){
        Intent intent = new Intent(UserProfileActivity.this, EditUserEmailActivity.class);
        isTransition = true;
        startActivity(intent);
    }

    public void changeUserLocation(View confirmationButton){
        Intent intent = new Intent(UserProfileActivity.this, EditUserLocationActivity.class);
        isTransition = true;
        startActivity(intent);
    }

    public void removeUserAccount(View button){

        final EditText input = new EditText(UserProfileActivity.this);
        input.setHint(R.string.prompt_password);
        input.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(20,10,20,10);
        input.setLayoutParams(lp);

        new AlertDialog.Builder(this)
                .setView(input)
                .setIcon(R.drawable.ic_report_problem_black_24dp)
                .setTitle(getString(R.string.remove_user_account))
                .setMessage(getString(R.string.remove_user_account_msg))
                .setPositiveButton(getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Firebase appRef = new Firebase(getString(R.string.firebase_url));

                        String password = input.getText().toString();

                        if (!ValidationTools.isValidPassword(password)) {

                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_empty_password), Toast.LENGTH_LONG);

                        }else{
                            String userId = userPreferences.getString(getString(R.string.userid_key), "");
                            String userEmail = userPreferences.getString(getString(R.string.useremail_key), "");

                            appRef.child(getString(R.string.child_users)).child(userId).setValue(null);

                            appRef.removeUser(userEmail, password, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    SharedPreferences busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), Context.MODE_PRIVATE);

                                    String businessId = busPreferences.getString(getString(R.string.businessid_key), "");

                                    if (!businessId.isEmpty()) {
                                        appRef.child(getString(R.string.child_business)).child(businessId).setValue(null);
                                        appRef.child(getString(R.string.child_business_details)).child(businessId).setValue(null);
                                        appRef.child(getString(R.string.child_business_statistics)).child(businessId).setValue(null);
                                        busPreferences.edit().clear().commit();
                                    }
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_failure_remove_user), Toast.LENGTH_LONG);

                                }
                            });

                            userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), Context.MODE_PRIVATE);
                            userPreferences.edit().clear().commit();

                            appRef.unauth();
                            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                            isTransition = true;
                            finish();
                            startActivity(intent);
                        }
                    }

                })
                .setNegativeButton(getString(R.string.alert_dialog_negative), null)
                .show();

    }

}
