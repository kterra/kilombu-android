package kilombu.kilombuapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static boolean hasActiveNetworkConnection(Context context){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

}
