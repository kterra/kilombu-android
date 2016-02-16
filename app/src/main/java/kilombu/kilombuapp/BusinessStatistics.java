package kilombu.kilombuapp;

import java.util.Map;

/**
 * Created by hallpaz on 15/02/2016.
 */
public class BusinessStatistics {
    private long clicksCount;
    private long evaluationsCount;
    private float evaluation;
    private long timestamp;

    BusinessStatistics(){
        clicksCount = 0;
        evaluationsCount = 0;
        evaluation = 0.0f;
    }

    public long getClicksCount() {
        return clicksCount;
    }

    public long getEvaluationsCount() {
        return evaluationsCount;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

/*
private void displayDate(){
        Firebase statisticsRef = new Firebase(getString(R.string.firebase_url))
                .child(getString(R.string.child_business_statistics));

        Query businessStatistics = statisticsRef.orderByKey().equalTo(businessKey);
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
