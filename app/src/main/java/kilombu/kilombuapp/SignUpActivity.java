package kilombu.kilombuapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends Activity {

    private Firebase appRef;
    private Firebase usersRef;
    private ProgressDialog dialog;
    private final String TAG = "SignUp";
    private User newuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        appRef = new Firebase(getString(R.string.firebase_url));
        usersRef = appRef.child("users");
    }

    public void signup(View view){
        EditText nameEditText = (EditText) findViewById(R.id.username_edit_text);
        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        EditText passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        if(validateData(name, email, password, passwordAgain)){
            newuser = new User(name, email, null);
        }

        // Set up a progress dialog
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        createUser(email, password);
    }

    public void createUser(String email, String password){
        appRef.createUser(email, password,
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result){
                        Log.d(TAG, "Created user with uid: " + result.get("uid"));
                        Firebase newuserRef = usersRef.child(result.get("uid").toString());
                        newuserRef.setValue(newuser);
                        Toast.makeText(SignUpActivity.this, result.get("uid").toString(), Toast.LENGTH_LONG).show();
                        dialog.hide();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError){
                        Log.e(TAG, firebaseError.toString());
                        Toast.makeText(SignUpActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
                        dialog.hide();
                    }
                });
    }

    private boolean validateData(String name, String email, String password, String passwordAgain) {
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (name.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }


        if (email.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }

        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
        }
        return validationError;
    }
}
