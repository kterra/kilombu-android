package kilombu.kilombuapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kilombu.kilombuapp.models.Business;

/**
 * Created by hallpaz on 11/10/2015.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.BusinessViewHolder> {
    List<Business> businesses;
    static Context context;

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView businessName;
        TextView shortDescription;

        BusinessViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            businessName = (TextView)itemView.findViewById(R.id.business_name);
            shortDescription = (TextView)itemView.findViewById(R.id.business_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdsAdapter.context, BusinessDetailsActivity.class);

                    intent.putExtra("business_name", businessName.getText());
                    intent.putExtra("business_description", shortDescription.getText());
                    AdsAdapter.context.startActivity(intent);

                }
            });

        }
    }

    AdsAdapter(Context context, List<Business> businesses){

        this.businesses = businesses;
        AdsAdapter.context = context;
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
        businessViewHolder.shortDescription.setText(businesses.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
}
