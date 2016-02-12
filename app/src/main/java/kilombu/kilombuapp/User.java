package kilombu.kilombuapp;

/**
 * Created by hallpaz on 12/10/2015.
 */
public class User {

    private String name;
    private String email;
    private String city;

    User(){

    }

    User(String name, String email, String city) {
        this.name = name;
        this.email = email;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }
}
