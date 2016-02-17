package kilombu.kilombuapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

public class UserInfoActivity extends AppCompatActivity {

    private Firebase appRef;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appRef = new Firebase(getString(R.string.firebase_url));

        if( appRef.getAuth() != null){
            String userId = appRef.getAuth().getUid();
            //set nav header text
            Query getUser = appRef.child("users").orderByKey().equalTo(userId);
            getUser.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()) {
                           Log.d("auth", Long.toString(dataSnapshot.getChildrenCount()));
                           currentUser = dataSnapshot.getChildren().iterator().next().getValue(User.class);

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

    private void changeUserName(View confirmationButton){
        Map<String, Object> name = new HashMap<String, Object>();
        String newUserName = "";
        name.put("name", newUserName);
        appRef.child(getString(R.string.child_users))
                .child(appRef.getAuth().getUid()).updateChildren(name);
        Toast.makeText(UserInfoActivity.this, R.string.toast_success_change_email, Toast.LENGTH_LONG).show();
    }

    private void changeUserPassword(View confirmationButton){
        String oldPassword = "";
        String newPassword = "";
        appRef.changePassword(currentUser.getEmail(), oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserInfoActivity.this, R.string.toast_success_change_password, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(UserInfoActivity.this, R.string.toast_failure_change_password, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeUserEmail(View confirmationButton){
        String password = "";
        final String newEmail = "";
        appRef.changeEmail(currentUser.getEmail(), password, newEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Map<String, Object> email = new HashMap<String, Object>();
                email.put("email", newEmail);
                appRef.child(getString(R.string.child_users))
                        .child(appRef.getAuth().getUid()).updateChildren(email);
                Toast.makeText(UserInfoActivity.this, R.string.toast_success_change_email, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(UserInfoActivity.this, R.string.toast_failure_change_email, Toast.LENGTH_LONG).show();
            }
        });
    }

}
