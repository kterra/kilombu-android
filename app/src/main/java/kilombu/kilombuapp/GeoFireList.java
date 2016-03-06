package kilombu.kilombuapp;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.util.ArrayList;

/**
 * Created by hallpaz on 05/03/2016.
 */
public class GeoFireList implements GeoQueryEventListener {

    private GeoQuery mQuery;
    private ArrayList<DataSnapshot> mSnapshots;


    public GeoFireList(GeoQuery query){

    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryError(FirebaseError error) {

    }
}
