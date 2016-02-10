package kilombu.kilombuapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hallpaz on 11/10/2015.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.BusinessViewHolder> {

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView businessName;
        //TextView userAge;
        TextView shortDescription;
        ImageView userPhoto;

        BusinessViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            businessName = (TextView)itemView.findViewById(R.id.user_name);
            //userAge = (TextView)itemView.findViewById(R.id.person_age);
            userPhoto = (ImageView)itemView.findViewById(R.id.user_photo);
            shortDescription = (TextView)itemView.findViewById(R.id.user_comment);
        }
    }

    List<Business> businesses;

    AdsAdapter(List<Business> businesses){
        this.businesses = businesses;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BusinessViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        BusinessViewHolder pvh = new BusinessViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BusinessViewHolder businessViewHolder, int position) {
        businessViewHolder.businessName.setText(businesses.get(position).getName());
        //userViewHolder.userAge.setText(users.get(position).age);
        //businessViewHolder.userPhoto.setImageResource(businesses.get(position).photoId);
        businessViewHolder.shortDescription.setText(businesses.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
}
