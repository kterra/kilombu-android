package kilombu.kilombuapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private final String TAG = "SignUp";
    private User newuser;
    private EditText nameEditText, emailEditText, passwordEditText, passwordAgainEditText;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutPasswordAgain;
    private boolean isTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_signup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        nameEditText = (EditText) findViewById(R.id.username_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);

        inputLayoutName = (TextInputLayout) findViewById(R.id.username_edit_layout);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.email_edit_layout);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.password_edit_layout);
        inputLayoutPasswordAgain = (TextInputLayout) findViewById(R.id.password_again_edit_layout);
    }

    public void signup(View view) {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        if (!validateName(name)) {
            return;
        }

        if (!validateEmail(email)) {
            return;
        }

        if (!validatePassword(password)) {
            return;
        }

        if (!validatePasswordAgain(password,passwordAgain)) {
            return;
        }

        newuser = new User(name, email, null);

        // Set up a progress dialog
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        createUser(email, password);
        Toast.makeText(getApplicationContext(), "Bem-vind@ à família Kilombu!", Toast.LENGTH_SHORT).show();
    }

    public void createUser(String email, String password){
        final Firebase appRef = new Firebase(getString(R.string.firebase_url));
        appRef.createUser(email, password,
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result){
                        Log.d(TAG, "Created user with uid: " + result.get("uid"));
                        Firebase newuserRef = appRef.child(getString(R.string.child_users))
                                .child(result.get("uid").toString());
                        newuserRef.setValue(newuser);
                        Log.d(TAG, result.get("uid").toString());
                        dialog.hide();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        isTransition = true;
                        startActivity(intent);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError){
                        Log.e(TAG, firebaseError.toString());
                        Toast.makeText(SignUpActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
                        dialog.hide();
                    }
                });
    }

    private boolean validateName(String name) {

        if (!ValidationTools.isValidName(name)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(nameEditText);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
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

    private boolean validateEmail(String email) {

        if (email.isEmpty() || !ValidationTools.isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(emailEditText);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String password) {

        if (!ValidationTools.isValidPassword(password)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordEditText);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordAgain(String password, String passwordAgain) {

        if (!ValidationTools.isValidPasswordAgain(password, passwordAgain)) {
            inputLayoutPasswordAgain.setError(getString(R.string.err_msg_password_again));
            requestFocus(passwordAgainEditText);
            return false;
        } else {
            inputLayoutPasswordAgain.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
