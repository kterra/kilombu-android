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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class UserProfileActivity extends AppCompatActivity {


    TextView usernameProfile, emailProfile;
    private SharedPreferences userPreferences;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_user_profile));

        context = UserProfileActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        String userName = userPreferences.getString(getString(R.string.username_key), "");
        String userEmail = userPreferences.getString(getString(R.string.useremail_key), "");

        usernameProfile = (TextView) findViewById(R.id.user_name_profile);
        usernameProfile.setText(userName);
        emailProfile = (TextView) findViewById(R.id.email_profile);
        emailProfile.setText(userEmail);

    }

    public void changeName(View button){
        Intent intent = new Intent(UserProfileActivity.this, EditUserNameActivity.class);
        startActivity(intent);
    }

    public void changeUserPassword(View confirmationButton){

        Intent intent = new Intent(UserProfileActivity.this, ChangePassword.class);
        startActivity(intent);
    }

    public void changeUserEmail(View confirmationButton){
        Intent intent = new Intent(UserProfileActivity.this, EditUserEmailActivity.class);
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
                .setMessage(getString(R.string.remove_user_account_message))
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
                            Log.d("MAIN",userEmail);
                            Log.d("MAIN", password);

                            appRef.removeUser(userEmail, password, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    SharedPreferences busPreferences = context.getSharedPreferences(getString(R.string.preference_business_key), Context.MODE_PRIVATE);

                                    String businessId = busPreferences.getString(getString(R.string.businessid_key), "");

                                    if (businessId.isEmpty()) {
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
                            finish();
                            startActivity(intent);
                        }
                    }

                })
                .setNegativeButton(getString(R.string.alert_dialog_negative), null)
                .show();

    }

}
