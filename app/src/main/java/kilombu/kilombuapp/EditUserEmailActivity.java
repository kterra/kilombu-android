package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class EditUserEmailActivity extends AppCompatActivity {

    private String userId, currentUserEmail, newUserEmail, userPassword;
    private TextView currentUserEmailField;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private SharedPreferences userPreferences;
    private android.content.Context context;
    private Firebase appRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_user_email));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        appRef = new Firebase(getString(R.string.firebase_url));

        context = EditUserEmailActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        userId = userPreferences.getString(getString(R.string.userid_key), "");
        currentUserEmail = userPreferences.getString(getString(R.string.useremail_key), "");


        currentUserEmailField = (TextView) findViewById(R.id.current_email);
        currentUserEmailField.setText(currentUserEmail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserEmail();

            }
        });
    }


    private void changeUserEmail(){
       userPassword = ((EditText) findViewById(R.id.password)).getText().toString().trim();
       newUserEmail = ((EditText) findViewById(R.id.new_useremail)).getText().toString().trim();

        if (!validateEmail(newUserEmail)) {
            return;
        }

        if (!validatePassword(userPassword)) {
            return;
        }

        appRef.changeEmail(currentUserEmail, userPassword, newUserEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Map<String, Object> email = new HashMap<String, Object>();
                email.put(getString(R.string.child_details_this_email), newUserEmail);
                appRef.child(getString(R.string.child_users))
                        .child(userId).updateChildren(email);

                userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
                SharedPreferences.Editor userEditor = userPreferences.edit();
                userEditor.putString(getString(R.string.useremail_key), newUserEmail);
                userEditor.commit();

                Intent intent = new Intent(EditUserEmailActivity.this, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(EditUserEmailActivity.this, R.string.toast_failure_change_email, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateEmail(String email) {

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.new_email_layout);

        if (!ValidationTools.isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus((EditText) findViewById(R.id.new_useremail));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String password) {

        inputLayoutPassword = (TextInputLayout) findViewById(R.id.password_layout);

        if (!ValidationTools.isValidPassword(password)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus((EditText) findViewById(R.id.password));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
