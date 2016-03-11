package kilombu.kilombuapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import kilombu.kilombuapp.models.BusinessAddress;
import kilombu.kilombuapp.models.Store;

/**
 * Created by kizzyterra on 17/02/16.
 */
public class ValidationTools {

    public static final long bstLaunchTimestamp = 1456009200;

    public static int convertCategory(String category, Context context){
        if (category.equals(context.getString(R.string.category_all))){
            return 0;
        }
        ArrayList<String> categories = new ArrayList<String >(
                Arrays.asList(context.getResources().getStringArray(R.array.categories)));
        return categories.indexOf(category);
    }

    public static String categoryForIndex(int i, Context context) {
        if (i == 0){
            return context.getString(R.string.category_all);
        }
        String[] categories = context.getResources().getStringArray(R.array.categories);
        return categories[i];
    }

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

    public static boolean isValidPassword(String password) {

        if (password.isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean isValidPasswordAgain(String password, String passwordAgain){
        if(passwordAgain.isEmpty() ||
                passwordAgain.compareTo(password)!=0){
            return false;
        }
        return true;
    }

    public static BusinessAddress validateAddress(String street, String complement, String district, String city, String state){
        if (!city.isEmpty() || !street.isEmpty() || !district.isEmpty()){
            return new BusinessAddress(street, complement, district, city, state);
        }
        return null;
    }

    public static Store validateStore(BusinessAddress address, String phone, String hours){
        if (address != null || !phone.isEmpty() || !hours.isEmpty()){
            return  new Store(address, phone, hours);
        }
        return null;
    }

}
