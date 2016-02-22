package kilombu.kilombuapp;

/**
 * Created by hallpaz on 09/02/2016.
 */
public class Business {

    public static final int categoryOffset = 1000000;
    private static final int initialDecrement = 10;
    private String admin;
    private int category;
    private String description;
    private String name;
    private String corporateNumber;
    private double categoryRankPoints;
    private double rankPoints;


    public Business(){
    }

    public Business(String name, String admin, int category, String description, String corporateNumber){

        this.name = name;
        this.admin = admin;
        this.category = category;
        this.description = description;

        this.corporateNumber = corporateNumber;
        if (admin == null){
            this.rankPoints = categoryOffset-1;
        }
        else{
            long nowTimestamp = System.currentTimeMillis()/1000;
            this.rankPoints =  categoryOffset - ((double)categoryOffset/nowTimestamp)*1000 - initialDecrement;
        }
        this.categoryRankPoints = this.category * categoryOffset + this.rankPoints - initialDecrement;

    }

    public String getAdmin() {
        return admin;
    }

    public int getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getCorporateNumber() {
        return corporateNumber;
    }

    public double getRankPoints() {
        return rankPoints;
    }

    public double getCategoryRankPoints() {
        return categoryRankPoints;
    }
}

