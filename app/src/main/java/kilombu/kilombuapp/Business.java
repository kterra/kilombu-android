package kilombu.kilombuapp;

/**
 * Created by hallpaz on 09/02/2016.
 */
public class Business {

    private String admin;
    private int category;
    private String description;
    private String name;
    private String corporateNumber;
    private double categoryRankPoints;
    private double rankPoints;


    public Business(){
    }

    public Business(String name, String admin, String category, String description, String corporateNumber){

        this.name = name;
        this.admin = admin;
        this.category = ValidationTools.convertCategory(category);
        this.description = description;

        this.corporateNumber = corporateNumber;
        if (admin == null){
            this.rankPoints = ValidationTools.bstLaunchTimestamp*1000;
        }
        else{
            long nowTimestamp = System.currentTimeMillis()/1000;
            this.rankPoints = ValidationTools.bstLaunchTimestamp*1000 -
                    ((double)ValidationTools.bstLaunchTimestamp/nowTimestamp)*100;
        }
        this.categoryRankPoints = this.category * ValidationTools.categoryOffset + this.rankPoints;

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

