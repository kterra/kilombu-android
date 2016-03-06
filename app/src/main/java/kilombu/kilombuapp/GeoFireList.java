package kilombu.kilombuapp;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.util.GeoUtils;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hallpaz on 05/03/2016.
 */
public class GeoFireList implements GeoQueryEventListener {
    private static int globalIndex = 0;

    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved, Ready }
        void onChanged(EventType type, int index, int oldIndex);
    }

    private final String TAG = "GeoFireList";
    private GeoQuery mQuery;
    private ArrayList<DataSnapshot> mSnapshots;
    private SortedMap<Double, String> distanceAndKeys;
    private OnChangedListener mListener;
    private Firebase modelRef;

    public GeoFireList(GeoQuery query, Firebase ref){
        mQuery = query;
        modelRef = ref;
        distanceAndKeys = new TreeMap<Double, String>();
        mSnapshots = new ArrayList<DataSnapshot>();
        mQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        double distance = GeoUtils.distance(location, mQuery.getCenter());
        distanceAndKeys.put(distance, key);
    }

    @Override
    public void onGeoQueryReady() {
        for (String key: distanceAndKeys.values()) {
            modelRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int index = mSnapshots.size();
                    mSnapshots.add(dataSnapshot);
                    notifyChangedListeners(OnChangedListener.EventType.Added, index);
                    Log.d(TAG, index + " Retrieving: " + dataSnapshot.getKey());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e(TAG, "key entered cancelled");
                }
            });
        }
        notifyChangedListeners(OnChangedListener.EventType.Ready, -1);
    }

    @Override
    public void onKeyExited(String key) {
        int index = getIndexForKey(key);
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.Removed, index);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.i(TAG, "Key: " + key + " moved to: " + location.toString());
    }

    @Override
    public void onGeoQueryError(FirebaseError error) {
        Log.e(TAG, "Geo query error: " + error.getMessage());
    }

    public int getCount(){
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public void cleanup(){
        mQuery.removeAllListeners();
        //mQuery.removeEventListener(this);
    }

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    private int getIndexBasedOnDistance(double distance){
        if (mSnapshots.isEmpty()){
            return 0;
        }

        return 0;
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }
}
