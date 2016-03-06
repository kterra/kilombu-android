package kilombu.kilombuapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hallpaz on 12/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String name;
    private String email;

    User(){

    }

    User(String name, String email, String city) {
        this.name = name;
        this.email = email;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}
