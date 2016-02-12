package kilombu.kilombuapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

public class DispatchActivity extends Activity {

    private Firebase appRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appRef = new Firebase(getString(R.string.firebase_url));

        /* Redirection Logic
        if(ParseUser.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }*/
    }

}
