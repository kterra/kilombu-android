/**
 * Created by hallpaz on 05/03/2016.
 *
 * based on FirebaseRecyclerAdapter from Firebase UI project
 * https://github.com/firebase/FirebaseUI-Android/blob/master/library/src/main/java/com/firebase/ui/FirebaseRecyclerAdapter.java
 */

package kilombu.kilombuapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class GeoFireRecyclerAdapter <T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    Class<T> mModelClass;
    protected int mModelLayout;
    Class<VH> mViewHolderClass;
    GeoFireList mSnapshots;
    private boolean geolistIsReady;

    /**
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param georef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public GeoFireRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass,
                                  GeoQuery georef, Firebase modelref) {
        mModelClass = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
        mSnapshots = new GeoFireList(georef, modelref);
        geolistIsReady = false;

        mSnapshots.setOnChangedListener(new GeoFireList.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type) {
                    case Added:
                        notifyItemInserted(index);
                        break;
                    case Changed:
                        notifyItemChanged(index);
                        break;
                    case Removed:
                        notifyItemRemoved(index);
                        break;
                    case Moved:
                        notifyItemMoved(oldIndex, index);
                        break;
                    case Ready:
                        geolistIsReady = true;
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }
        });
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.getCount();
    }

    public T getItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public Firebase getRef(int position) { return mSnapshots.getItem(position).getRef(); }



    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if ((position >= getItemCount() - 1) && geolistIsReady){
            Log.e("View Holder", "last pos: " + position);
            if (mSnapshots.getQueryRadius() < 300){
                mSnapshots.incrementQueryRadius();
            }
        }
        T model = getItem(position);
        if (model == null){
            Log.e("Geo Adapter", "Null em: " + position + " total: " + mSnapshots.getCount() + "key: " + mSnapshots.getItem(position).getKey());
        }
        populateViewHolder(viewHolder, model, position);
    }

    public void updateQueryCenter(GeoLocation center){
        mSnapshots.updateQueryCenter(center);
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position  The position in the list of the view being populated
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);

}