package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private boolean isTransition = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTemporaryToken();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isTransition = false;
        Firebase.goOnline();
        Log.d("MAIN", "ON START");
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

    private void sendTemporaryToken(){
        EditText emailField = (EditText) findViewById(R.id.registered_email);
        final String email = emailField.getText().toString().trim();

        if (validateEmail(email)) {
            Firebase appRef = new Firebase(getString(R.string.firebase_url));
            appRef.resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Instruções enviadas para " + email, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(ResetPasswordActivity.this,
                            getString(R.string.toast_action_not_completed), Toast.LENGTH_LONG).show();
                }
            });
            Intent intent = new Intent(this, ChangeTokenPasswordActivity.class);
            intent.putExtra("userEmail",email);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            isTransition = true;
            startActivity(intent);

        }
    }

    private boolean validateEmail(String email) {

        TextInputLayout inputLayoutEmail = (TextInputLayout) findViewById(R.id.registered_email_layout);

        if (!ValidationTools.isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus((EditText) findViewById(R.id.registered_email));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
