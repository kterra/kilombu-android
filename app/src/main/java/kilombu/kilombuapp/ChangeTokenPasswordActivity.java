package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class ChangeTokenPasswordActivity extends AppCompatActivity {

    private String userEmail;
    private EditText passwordField, newPasswordField, newPasswordAgainField;
    private Firebase appRef;
    private TextInputLayout inputLayoutPassword,  inputLayoutNewPassword, inputLayoutNewPasswordAgain;
    private SharedPreferences userPreferences;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_token_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_change_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        appRef = new Firebase(getString(R.string.firebase_url));

        userEmail = getIntent().getStringExtra("userEmail");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserPassword();
            }
        });
    }

    private void changeUserPassword(){

        passwordField = (EditText)findViewById(R.id.token);
        String password = passwordField.getText().toString();

        newPasswordField = (EditText)findViewById(R.id.new_password);
        String newPassword = newPasswordField.getText().toString();

        newPasswordAgainField = (EditText)findViewById(R.id.new_password_again);
        String newPasswordAgain = newPasswordAgainField.getText().toString();

        if(!validatePassword(password)){
            return;
        }
        if(!validateNewPassword(newPassword)){
            return;
        }
        if(!validateNewPasswordAgain(newPassword, newPasswordAgain)){
            return;
        }

        appRef.changePassword(userEmail, password, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(ChangeTokenPasswordActivity.this, R.string.toast_success_change_password, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ChangeTokenPasswordActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(ChangeTokenPasswordActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(ChangeTokenPasswordActivity.this, R.string.toast_failure_change_password, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ChangeTokenPasswordActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private boolean validatePassword(String password) {
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.token_layout);
        if (!ValidationTools.isValidPassword(password)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordField);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNewPassword(String password) {

        inputLayoutNewPassword = (TextInputLayout) findViewById(R.id.new_password_layout);

        if (!ValidationTools.isValidPassword(password)) {
            inputLayoutNewPassword.setError(getString(R.string.err_msg_password));
            requestFocus(newPasswordField);
            return false;
        } else {
            inputLayoutNewPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNewPasswordAgain(String password, String passwordAgain) {

        inputLayoutNewPasswordAgain = (TextInputLayout) findViewById(R.id.new_password_again_layout);

        if (!ValidationTools.isValidPasswordAgain(password, passwordAgain)) {
            inputLayoutNewPasswordAgain.setError(getString(R.string.err_msg_password_again));
            requestFocus(newPasswordAgainField);
            return false;
        } else {
            inputLayoutNewPasswordAgain.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
