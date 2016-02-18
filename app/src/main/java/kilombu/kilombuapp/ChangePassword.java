package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class ChangePassword extends AppCompatActivity {

    private String userId, userEmail, currentPassword, newPassword, newPasswordAgain;
    private EditText passwordField, newPasswordField, newPasswordAgainField;
    private Firebase appRef;
    private TextInputLayout inputLayoutPassword,  inputLayoutNewPassword, inputLayoutNewPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.title_activity_change_password));

        appRef = new Firebase(getString(R.string.firebase_url));
        setup();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserPassword();
            }
        });
    }

    private void setup(){
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userEmail = intent.getStringExtra("userEmail");

    }

    private void changeUserPassword(){

        passwordField = (EditText)findViewById(R.id.password);
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
                Intent intent = new Intent(ChangePassword.this, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(ChangePassword.this, R.string.toast_success_change_password, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(ChangePassword.this, R.string.toast_failure_change_password, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validatePassword(String password) {
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.password_layout);
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
