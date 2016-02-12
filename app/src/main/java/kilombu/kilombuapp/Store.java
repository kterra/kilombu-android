package kilombu.kilombuapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hallpaz on 12/02/2016.
 */
public class Store {

    private BusinessAddress address;
    private String phoneNumber;
    private String businessHours;

    public Store(){

    }

    public Store(BusinessAddress businessAddress, String phoneNumber, String businessHours) {
        this.address = businessAddress;
        this.phoneNumber = phoneNumber;
        this.businessHours = businessHours;
    }

    public BusinessAddress getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBusinessHours() {
        return businessHours;
    }
}
