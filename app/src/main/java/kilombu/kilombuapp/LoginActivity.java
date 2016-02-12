package kilombu.kilombuapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Firebase appRef;
    private static final String TAG = "LoginActivity";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);

        appRef = new Firebase(getString(R.string.firebase_url));

    }

    public void login(View view){
        // Set up a progress dialog

        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        EditText emailEditText = (EditText) findViewById(R.id.login_email);
        EditText passwordEditText = (EditText) findViewById(R.id.login_password);

        //createUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
        loginWithPassword(emailEditText.getText().toString(), passwordEditText.getText().toString());

        /*Login Logic
        ParseUser.logInInBackground(emailEditText.getText().toString(),
                passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });*/
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
            dialog.hide();
            Log.i(TAG, provider + " auth successful");
            Toast.makeText(LoginActivity.this, authData.getUid().toString(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            dialog.hide();
            Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
        }
    }



    public void loginWithPassword(String username, String password) {
        dialog.show();
        appRef.authWithPassword(username, password, new AuthResultHandler("password"));
    }

    public void gotosignup(View view){
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        startActivity(new Intent(this, SignUpActivity.class));
    }
}
