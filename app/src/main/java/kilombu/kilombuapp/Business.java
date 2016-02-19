package kilombu.kilombuapp;

/**
 * Created by hallpaz on 09/02/2016.
 */
public class Business {

    private String admin;
    private String category;
    private String description;
    private String name;
    private String corporateNumber;
    private String categoryRankPoints;
    private double rankPoints;


    public Business(){
    }

    public Business(String name, String admin, String category, String description, String corporateNumber){

        this.name = name;
        this.admin = admin;
        this.category = category;
        this.description = description;

        this.corporateNumber = corporateNumber;
        this.rankPoints = 0.0;

        this.categoryRankPoints = category + rankPoints;
    }

    public String getAdmin() {
        return admin;
    }

    public String getCategory() {
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

    public String getCategoryRankPoints() {
        return categoryRankPoints;
    }
}

