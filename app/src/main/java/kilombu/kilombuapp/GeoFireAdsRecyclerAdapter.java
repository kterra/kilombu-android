package kilombu.kilombuapp;

/**
 * Created by hallpaz on 06/03/2016.
 */
/*public class GeoFireAdsRecyclerAdapter extends GeoFireRecyclerAdapter<Business, RecyclerView.ViewHolder> {

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView businessName;
        TextView shortDescription;

        public BusinessViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            businessName = (TextView)itemView.findViewById(R.id.business_name);
            shortDescription = (TextView)itemView.findViewById(R.id.business_description);

        }
    }
    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Business business, int position) {
       *//* if (business == null){ //footer
            //Log.d("FOOTER POS", Integer.toString(position));
            int footerPosition = getItemCount() -1;
            //Log.d("FOOTER ITENS", Integer.toString(getItemCount()));

            footerViewHolder = (FooterViewHolder) viewHolder;
            if (currentPage == 1){
                footerViewHolder.navigateBackLayout.setVisibility(View.GONE);
            }else{
                footerViewHolder.navigateBackLayout.setVisibility(View.VISIBLE);
            }

            footerViewHolder.navigateNextLayout.setVisibility(View.VISIBLE);

        }
        else {*//*

            String businessName = business.getName();

            BusinessViewHolder businessViewHolder = (BusinessViewHolder) viewHolder;
            businessViewHolder.businessName.setText(businessName);
            String description = business.getDescription();
            if(description!= null && description.length() > 300){

                description = description.substring(0,299);
                description = description + " ...";

            }

            businessViewHolder.shortDescription.setText(description);

           *//* if(businessName.equals(getString(R.string.no_ads_left))){

                CardView cv = businessViewHolder.cv;

                cv.setVisibility(View.VISIBLE);
                RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                childView.findViewById(R.id.cardview_arrow).setVisibility(View.GONE);
                ((TextView)childView.findViewById(R.id.business_description)).setTextSize(12);


                TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                businessNameView.setTextSize(16);

                View view =  (View) relativeLayoutView.getChildAt(1);
                view.setVisibility(View.GONE);

                *//**//*if (footerViewHolder.navigateNextLayout != null){
                    footerViewHolder.navigateNextLayout.setVisibility(View.GONE);
                }*//**//*


            }else{*//*
                CardView cv = businessViewHolder.cv;
                cv.setVisibility(View.VISIBLE);
                cv.requestFocus();
                RelativeLayout relativeLayoutView = (RelativeLayout) cv.getChildAt(0);
                LinearLayoutCompat childView = (LinearLayoutCompat) relativeLayoutView.getChildAt(2);
                childView.findViewById(R.id.cardview_arrow).setVisibility(View.VISIBLE);
                ((TextView)childView.findViewById(R.id.business_description)).setTextSize(16);


                TextView businessNameView =  (TextView) relativeLayoutView.getChildAt(0);
                businessNameView.setTextSize(20);

                View view =  (View) relativeLayoutView.getChildAt(1);
                view.setVisibility(View.VISIBLE);

            //}



            businessViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("NOME", business.getName());
                   *//* if (business.getName().equals(getString(R.string.no_ads_left))) {
                        //Log.d("IF", "RETORNA");
                        return;
                    }*//*
                    Intent intent = new Intent(MainActivity.this, BusinessDetailsActivity.class);

                    intent.putExtra("business_name", business.getName());
                    intent.putExtra("business_category", business.getCategory());
                    intent.putExtra("business_description", business.getDescription());

                    Firebase itemRef = firebaseAdsAdapter.getRef(position);
                    String itemKey = itemRef.getKey();
                    intent.putExtra("businessId", itemKey);
                    isTransition = true;
                    MainActivity.this.startActivity(intent);

                }
            });

                *//*Log.d("POSITION", Integer.toString(position));
                Log.d("COUNTS", Integer.toString(getItemCount()));*//*
            if (position > 0 || position == getItemCount() - 2) {
                loadingArea.setVisibility(View.GONE);


            }
    }
}*/
