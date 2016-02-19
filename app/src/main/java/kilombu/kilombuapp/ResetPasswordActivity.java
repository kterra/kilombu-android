package kilombu.kilombuapp;

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

public class ResetPasswordActivity extends AppCompatActivity {

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

    private void sendTemporaryToken(){
        EditText emailField = (EditText) findViewById(R.id.registered_email);
        final String email = emailField.getText().toString();

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
                            "Não foi possível completar sua solicitação", Toast.LENGTH_LONG).show();
                }
            });
            finish();
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
