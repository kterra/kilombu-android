package kilombu.kilombuapp;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hallpaz on 09/02/2016.
 */
public class Business {

    private String name;
    private String admin;
    private String category;
    private String description;

    private String CNPJ;

    public Business(){
    }

    public Business(String name, String admin, String CNPJ,
                    String category, String description){

        this.name = name;
        this.admin = admin;
        this.category = category;
        this.description = description;

        this.CNPJ = CNPJ;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
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

}
