package kilombu.kilombuapp;

import java.util.Map;

/**
 * Created by hallpaz on 12/02/2016.
 */
public class BusinessDetails {

    private Map<String, Store> stores;
    private String email;
    private String facebookPage;
    private String instagramPage;
    private String website;
    private String whatsapp;
    private String sacNumber;


    public BusinessDetails(){

    }

    public BusinessDetails(Map<String, Store> stores, String SACNumber, String email, String website,
                           String whatsapp, String facebookPage, String instagramPage ) {

        this.stores = stores;
        this.sacNumber = SACNumber;
        this.email = email;
        this.website = website;
        this.whatsapp = whatsapp;
        this.facebookPage = facebookPage;
        this.instagramPage = instagramPage;
    }

    public Map<String, Store> getStores() {
        return stores;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebookPage() {
        return facebookPage;
    }

    public String getInstagramPage() {
        return instagramPage;
    }

    public String getWebsite() {
        return website;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getSacNumber() {
        return sacNumber;
    }
}
