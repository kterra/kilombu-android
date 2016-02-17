package kilombu.kilombuapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    private EditText nameEditText, emailEditText, passwordEditText, passwordAgainEditText;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = (EditText) findViewById(R.id.username_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);

        inputLayoutName = (TextInputLayout) findViewById(R.id.username_edit_layout);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.email_edit_layout);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.password_edit_layout);
        inputLayoutPasswordAgain = (TextInputLayout) findViewById(R.id.password_again_edit_layout);

        appRef = new Firebase(getString(R.string.firebase_url));
        usersRef = appRef.child("users");
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
        Log.d(TAG, "Validou!!!");

        // Set up a progress dialog
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        createUser(email, password);
        Toast.makeText(getApplicationContext(), "Bem-vind@ à família Kilombu!", Toast.LENGTH_SHORT).show();
    }
//    public void signup(View view){
//        EditText nameEditText = (EditText) findViewById(R.id.username_edit_text);
//        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
//        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
//        EditText passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
//
//        String name = nameEditText.getText().toString().trim();
//        String email = emailEditText.getText().toString().trim();
//        String password = passwordEditText.getText().toString().trim();
//        String passwordAgain = passwordAgainEditText.getText().toString().trim();
//
//        if(validateData(name, email, password, passwordAgain)){
//            newuser = new User(name, email, null);
//            Log.d(TAG, "Validou!!!");
//        }
//
//        // Set up a progress dialog
//        dialog = new ProgressDialog(SignUpActivity.this);
//        dialog.setMessage(getString(R.string.progress_signup));
//        dialog.show();
//
//        createUser(email, password);
//    }

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
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

        if (ValidationTools.isValidName(name)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(nameEditText);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
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

        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordEditText);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordAgain(String password, String passwordAgain) {

        if (passwordAgain.isEmpty() ||
                passwordAgain.compareTo(password)!=0) {
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

//    private boolean validateData(String name, String email, String password, String passwordAgain) {
//        boolean validationError = false;
//        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
//        if (name.length() == 0) {
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_blank_username));
//        }
//
//
//        if (email.length() == 0) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_blank_email));
//        }
//
//        if (password.length() == 0) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_blank_password));
//        }
//        if (!password.equals(passwordAgain)) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
//        }
//        validationErrorMessage.append(getString(R.string.error_end));
//
//        if (validationError) {
//            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
//        }
//        return !validationError;
//    }
}
