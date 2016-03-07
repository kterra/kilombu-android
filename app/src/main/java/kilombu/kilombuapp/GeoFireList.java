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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hallpaz on 05/03/2016.
 */
public class GeoFireList implements GeoQueryEventListener {

    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved, Ready }
        void onChanged(EventType type, int index, int oldIndex);
    }

    private final String TAG = "GeoFireList";
    private GeoQuery mQuery;
    private ArrayList<DataSnapshot> mSnapshots;
    private ArrayList<Double> distances;
    private Map<String, Double> keysAndDistances;
    private OnChangedListener mListener;
    private Firebase modelRef;
    private final double radiusIncrement = 10.0;

    public GeoFireList(GeoQuery query, Firebase ref){
        mQuery = query;
        modelRef = ref;
        mSnapshots = new ArrayList<DataSnapshot>();
        distances = new ArrayList<Double>();
        mQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        double distance = GeoUtils.distance(location, mQuery.getCenter());
        //keysAndDistances.put(key, distance);
        final int index = indexBasedOnDistance(distance);
        distances.add(index, distance);
        modelRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //int index = mSnapshots.size();
                mSnapshots.add(index, dataSnapshot);
                notifyChangedListeners(OnChangedListener.EventType.Added, index);
                Log.d(TAG, index + " Retrieving: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "key entered cancelled");
            }
        });
    }

    public int indexBasedOnDistance(double distance){
        int index = 0;
        for (Double value : distances) {
            if (value > distance) {
                return index;
            } else {
                index++;
            }
        }
        return index;
    }

    @Override
    public void onGeoQueryReady() {
        if (getCount() == 0){
            if (mQuery.getRadius() < 700.0){
                incrementQueryRadius();
            }else{
                modelRef.child("Placeholder Location").addListenerForSingleValueEvent(new ValueEventListener() {
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
        }
        notifyChangedListeners(OnChangedListener.EventType.Ready, -1);
    }

    @Override
    public void onKeyExited(String key) {
        int index = getIndexForKey(key);
        mSnapshots.remove(index);
        distances.remove(index);
        //TODO: remove item from distance and key
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

    public void incrementQueryRadius(){
        double radius = mQuery.getRadius();
        Log.d(TAG, "Query radius: " + radius);
        mQuery.setRadius(radius*2);
    }

    public void updateQueryCenter(GeoLocation center){
        mQuery.setCenter(center);
    }

    public double getQueryRadius(){
        return mQuery.getRadius();
    }
}
