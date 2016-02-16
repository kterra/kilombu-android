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
    private double rankpoints;

    public Business(){
    }

    public Business(String name, String admin, String category, String description, String corporateNumber){

        this.name = name;
        this.admin = admin;
        this.category = category;
        this.description = description;

        this.corporateNumber = corporateNumber;
        this.rankpoints = 0.0;
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

    public double getRankpoints() {
        return rankpoints;
    }
}

