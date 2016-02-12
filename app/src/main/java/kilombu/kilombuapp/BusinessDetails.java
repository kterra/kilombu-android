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
    private String SACNumber;


    public BusinessDetails(){

    }

    public BusinessDetails(Map<String, Store> stores, String email, String facebookPage,
                           String instagramPage, String website, String whatsapp, String SACNumber) {
        this.stores = stores;
        this.email = email;
        this.facebookPage = facebookPage;
        this.instagramPage = instagramPage;
        this.website = website;
        this.whatsapp = whatsapp;
        this.SACNumber = SACNumber;
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

    public String getSACNumber() {
        return SACNumber;
    }
}
