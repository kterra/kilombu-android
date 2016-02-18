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

    public BusinessAddress(String street, String district, String city, String state) {

        this.street = street;
        this.district = district;
        this.city = city;
        this.state = state;
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

    public String getDistrict() {
        return district;
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
        if (state != null && !state.isEmpty())
            representation += " (" + state + ")";
        return representation;
    }
}
