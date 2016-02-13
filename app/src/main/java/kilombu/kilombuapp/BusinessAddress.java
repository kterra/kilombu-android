package kilombu.kilombuapp;

/**
 * Created by hallpaz on 12/02/2016.
 */
public class BusinessAddress {
    private String country;
    private String state;
    private String city;
    private String district;
    private String street;
    private String complement;

    public BusinessAddress(){

    }

    public BusinessAddress(String city, String district, String street) {
        this.city = city;
        this.district = district;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getComplement() {
        return complement;
    }


    @Override
    public String toString(){
        String representation = "";
        if (street != "" && street != null)
            representation += street;
        if (district != null && !district.isEmpty())
            representation += ", " + district;
        if (city != null && !city.isEmpty())
            representation += ", " + city;
        return representation;
    }
}
