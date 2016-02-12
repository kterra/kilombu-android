package kilombu.kilombuapp;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by hallpaz on 12/02/2016.
 */
public class KilombuApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        //Initializes firebase
        Firebase.setAndroidContext(this);
    }
}
