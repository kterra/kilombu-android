package kilombu.kilombuapp;

/**
 * Created by kizzyterra on 17/02/16.
 */
public class ValidationTools {
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name){
        if (name.isEmpty()){
            return false;
        }
        return true;
    }

    public static boolean isValidDescription(String description){
        if (description.isEmpty()){
            return false;
        }
        return  true;
    }

    public static BusinessAddress validateAddress(String street, String district, String city, String state){
        if (!city.isEmpty() || !street.isEmpty() || !district.isEmpty()){
            return new BusinessAddress(street, district, city, state);
        }
        return null;
    }

    public static Store validateStore(BusinessAddress address, String phone, String hours){
        if (address != null || !phone.isEmpty() || !hours.isEmpty()){
            return  new Store(address, phone, hours);
        }
        return null;
    }

    public static boolean isValidPasswordAgain(String password, String passwordAgain){
        if(passwordAgain.isEmpty() ||
                passwordAgain.compareTo(password)!=0){
            return false;
        }
        return true;
    }
}
