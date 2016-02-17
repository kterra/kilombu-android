package kilombu.kilombuapp;

/**
 * Created by kizzyterra on 17/02/16.
 */
public class ValidationTools {
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
