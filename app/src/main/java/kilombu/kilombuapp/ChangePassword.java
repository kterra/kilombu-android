package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class ChangePassword extends AppCompatActivity {

    private String userId, userEmail, currentPassword, newPassword, newPasswordAgain;
    private Firebase appRef;

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

        String oldPassword = "";
        String newPassword = "";
        appRef.changePassword(userEmail, oldPassword, newPassword, new Firebase.ResultHandler() {
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
}
