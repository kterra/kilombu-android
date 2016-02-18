package kilombu.kilombuapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {


    TextView usernameProfile, emailProfile;
    private SharedPreferences userPreferences;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_user_profile));

        context = UserProfileActivity.this;
        userPreferences = context.getSharedPreferences(getString(R.string.preference_user_key), android.content.Context.MODE_PRIVATE);
        String userName = userPreferences.getString(getString(R.string.username_key), "");
        String userEmail = userPreferences.getString(getString(R.string.useremail_key), "");

        usernameProfile = (TextView) findViewById(R.id.user_name_profile);
        usernameProfile.setText(userName);
        emailProfile = (TextView) findViewById(R.id.email_profile);
        emailProfile.setText(userEmail);

    }

    public void changeName(View button){
        Intent intent = new Intent(UserProfileActivity.this, EditUserNameActivity.class);
        startActivity(intent);
    }

    public void changeUserPassword(View confirmationButton){

        Intent intent = new Intent(UserProfileActivity.this, ChangePassword.class);
        startActivity(intent);
    }

    public void changeUserEmail(View confirmationButton){
        Intent intent = new Intent(UserProfileActivity.this, EditUserEmailActivity.class);
        startActivity(intent);
    }

}
