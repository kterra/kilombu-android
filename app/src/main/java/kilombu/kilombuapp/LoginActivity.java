package kilombu.kilombuapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ProgressDialog dialog;
    private boolean isTransition = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d("LOGIN", "ON START");
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (! isTransition){
            Firebase.goOffline();
            Log.d("LOGIN", "GOING OFFLINE");
        }else{
            Log.d("LOGIN", "TRANSITION");
        }
        Log.d("LOGIN", "ON STOP");
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

    public void login(View view){
        // Set up a progress dialog

        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        EditText emailEditText = (EditText) findViewById(R.id.login_email);
        EditText passwordEditText = (EditText) findViewById(R.id.login_password);

        loginWithPassword(emailEditText.getText().toString(), passwordEditText.getText().toString());

    }


    public void loginWithPassword(String username, String password) {
        dialog.show();
        Firebase appRef = new Firebase(getString(R.string.firebase_url));
        appRef.authWithPassword(username, password, new AuthResultHandler("password"));
    }

    public void gotosignup(View view){
        isTransition = true;
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void goToResetPassword(View button){
        isTransition = true;
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            Log.i(TAG, provider + " auth successful");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            isTransition = true;
            startActivity(intent);
            dialog.hide();

        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            dialog.hide();
            Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();

        }
    }
}
