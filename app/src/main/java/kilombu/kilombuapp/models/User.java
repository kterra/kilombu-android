package kilombu.kilombuapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hallpaz on 12/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String name;
    private String email;
    private String city;
    private String state;

    public User(){

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;

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

    public String getState() {
        return state;
    }

}
