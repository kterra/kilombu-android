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
