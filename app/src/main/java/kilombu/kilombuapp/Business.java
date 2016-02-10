package kilombu.kilombuapp;

/**
 * Created by hallpaz on 09/02/2016.
 */
public class Business {

    private String name;
    private String user;
    private String CNPJ;
    private String category;
    private String description;
    private String city;
    private String address;
    private String business_hours;
    private String phone_number;
    private String email;
    private String facebook_page = null;
    private String instagram_page = null;

    public Business(){
    }

    public Business(String name, String user, String CNPJ,
                    String category, String description, String city,
                    String address, String business_hours, String phone_number,
                    String email, String facebook_page, String instagram_page){

        this.name = name;
        this.user = user;
        this.category = category;
        this.description = description;
        this.city = city;
        this.address = address;

        this.CNPJ = CNPJ;

        this.business_hours = business_hours;
        this.phone_number = phone_number;
        this.email = email;
        this.facebook_page = facebook_page;
        this.instagram_page = instagram_page;

    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getBusiness_hours() {
        return business_hours;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebook_page() {
        return facebook_page;
    }

    public String getInstagram_page() {
        return instagram_page;
    }
}
