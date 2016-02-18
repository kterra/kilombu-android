package kilombu.kilombuapp;

/**
 * Created by kizzyterra on 17/02/16.
 */
public class ValidationTools {
    public static boolean isValidEmail(String email) {
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public static boolean isValidPassword(String password){

        if (password.isEmpty()){
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
}
