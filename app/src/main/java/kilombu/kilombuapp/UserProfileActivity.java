package kilombu.kilombuapp;

import android.content.Intent;
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

    private Firebase appRef;
    private User currentUser;
    private String userName, userEmail, userId;
    TextView usernameProfile, emailProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_user_profile));

        appRef = new Firebase(getString(R.string.firebase_url));

        if( appRef.getAuth() != null){
             userId = appRef.getAuth().getUid();
            //set nav header text
            Query getUser = appRef.child("users").orderByKey().equalTo(userId);
            getUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                           if (dataSnapshot.exists()) {
                                                               Log.d("auth", Long.toString(dataSnapshot.getChildrenCount()));
                                                               currentUser = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                                               userName = currentUser.getName();
                                                               userEmail = currentUser.getEmail();
                                                               setup();

                                                           }else{
                                                               //TODO: what if user data is not found?
                                                           }
                                                       }

                                                       @Override
                                                       public void onCancelled(FirebaseError firebaseError) {

                                                       }
                                                   }
            );
        }


    }

    private void setup(){

        usernameProfile = (TextView) findViewById(R.id.user_name_profile);
        usernameProfile.setText(userName);
        emailProfile = (TextView) findViewById(R.id.email_profile);
        emailProfile.setText(userEmail);


    }
    public void changeName(View button){
        Intent intent = new Intent(UserProfileActivity.this, EditUserNameActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    public void changeUserPassword(View confirmationButton){

        Intent intent = new Intent(UserProfileActivity.this, ChangePassword.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    public void changeUserEmail(View confirmationButton){
        Intent intent = new Intent(UserProfileActivity.this, EditUserEmailActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

}
