package kilombu.kilombuapp;

/**
 * Created by hallpaz on 15/02/2016.
 */
public class BusinessStatistics {
    private long visualizations;
    private long recommendations;
    private long timestamp;

    BusinessStatistics(){
        visualizations = 0;
        recommendations = 0;
    }

    public long getVisualizations() {
        return visualizations;
    }

    public long getRecommendations() {
        return recommendations;
    }


    public long getTimestamp() {
        return timestamp;
    }
}

/*
private void displayDate(){
        Firebase statisticsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_statistics));

        Query businessStatistics = statisticsRef.orderByKey().equalTo(businessId);
        businessStatistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    BusinessStatistics currentStatistics = dataSnapshot.getChildren().iterator().
                            next().getValue(BusinessStatistics.class);

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(currentStatistics.getTimestamp());
                    String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                    Toast.makeText(BusinessDetailsActivity.this,
                            date, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(BusinessDetailsActivity.this,
                            "Estatisticas indisponiveis", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(BusinessDetailsActivity.this,
                        "OH Shit!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
 */
