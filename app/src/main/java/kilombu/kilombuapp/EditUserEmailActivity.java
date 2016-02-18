package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    private Firebase appRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_user_email));

        appRef = new Firebase(getString(R.string.firebase_url));

        setup();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserEmail();

            }
        });
    }

    private void setup(){
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        currentUserEmail = intent.getStringExtra("userEmail");

        currentUserEmailField = (TextView) findViewById(R.id.current_email);
        currentUserEmailField.setText(currentUserEmail);

    }

    private void changeUserEmail(){
       userPassword = ((EditText) findViewById(R.id.password)).getText().toString();
       newUserEmail = ((EditText) findViewById(R.id.new_useremail)).getText().toString();

        appRef.changeEmail(currentUserEmail, userPassword, newUserEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Map<String, Object> email = new HashMap<String, Object>();
                email.put("email", newUserEmail);
                appRef.child(getString(R.string.child_users))
                        .child(userId).updateChildren(email);
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

}
