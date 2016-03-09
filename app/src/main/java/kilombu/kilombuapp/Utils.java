package kilombu.kilombuapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kilombu.kilombuapp.models.Business;
import kilombu.kilombuapp.models.BusinessAddress;
import kilombu.kilombuapp.models.BusinessDetails;

/**
 * Created by kizzyterra on 20/02/16.
 */
public class Utils {

    private static final String TAG = "Utils";
    public static final int LOCATION_REQUEST = 72;

    public static String getFirebaseError(int error){
        switch(error){
            case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
                return "";
            case FirebaseError.DENIED_BY_USER:
                return "Permissão negada!";
            case FirebaseError.DISCONNECTED:
                return "Operação abortada por falta de conexão";
            case FirebaseError.EMAIL_TAKEN:
                return "Este endereço de email já está em uso";
            case FirebaseError.EXPIRED_TOKEN:
                return "Você não tem mais permissão para acessar!";
            case FirebaseError.INVALID_AUTH_ARGUMENTS:
                return "Problema no endereço de email ou senha fornecidos";
            case FirebaseError.INVALID_CONFIGURATION:
                return "Ocorreu um problema (invalid config)";
            case FirebaseError.INVALID_CREDENTIALS:
                return "Email ou senha inválidos";
            case FirebaseError.INVALID_EMAIL:
                return "Ocorreu um erro. Email inválido!";
            case FirebaseError.INVALID_PASSWORD:
                return "Ocorreu um erro. Senha Inválida!";
            case FirebaseError.INVALID_TOKEN:
                return "Token inválido";
            case FirebaseError.LIMITS_EXCEEDED:
                return "Tempo limite excedido";
            case FirebaseError.MAX_RETRIES:
                return "Aguarde um tempo para fazer putra tentativa.";
            case FirebaseError.NETWORK_ERROR:
                return "Falha na sua conexão";
            case FirebaseError.OPERATION_FAILED:
                return "A operação falhou devido a problemas no servidor";
            case FirebaseError.PERMISSION_DENIED:
                return "Você não tem permissão para acessar";
            case FirebaseError.PREEMPTED:
                return "Ocorreu um problema (preempted)";
            case FirebaseError.PROVIDER_ERROR:
                return "Ocorreu um problema (provider error)";
            case FirebaseError.UNAVAILABLE:
                return "Serviço indisponível";
            case FirebaseError.UNKNOWN_ERROR:
                return "Erro desconhecido";
            case FirebaseError.USER_CODE_EXCEPTION:
                return "Ocorreu um problema (code exception)";
            case FirebaseError.USER_DOES_NOT_EXIST:
                return "Você não possui uma conta cadastrada";
            case FirebaseError.WRITE_CANCELED:
                return "Edição cancelada";
            default:
                return "Ocorreu um problema!";

        }
    }


    public static LatLng getLocationFromAddress(Context context, String strAddress){

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        }catch (IOException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, e.getMessage());
        }
        return  p1;
    }

    public static void createBusinessPlaceholders(Context context){
        ArrayList<String> categories = new ArrayList<String>(
                Arrays.asList(context.getResources().getStringArray(R.array.categories_list)));
        Firebase businessRef = new Firebase(context.getString(R.string.firebase_url))
                .child(context.getString(R.string.child_business));
        int index = 1;
        for (String category:categories) {
            String name = context.getString(R.string.no_ads_left);
            Business placeholder = new Business(name, null, index++, null, null);
            businessRef.child("Placeholder " + category).setValue(placeholder);
        }
    }

    public static void createBusinessLocation(final Context context) {
        final Firebase appRef = new Firebase(context.getString(R.string.firebase_url));

        Query businessQuery = appRef.child(context.getString(R.string.child_business));
        businessQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Business business = snapshot.getValue(Business.class);
                    final String id = snapshot.getKey();
                    final String category = ValidationTools.categoryForIndex(business.getCategory(), context);

                    appRef.child(context.getString(R.string.child_business_details))
                            .child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            BusinessDetails details = dataSnapshot.getValue(BusinessDetails.class);

                            try {
                                BusinessAddress address = details.getStores().values().iterator().next().getAddress();
                                LatLng latLng = Utils.getLocationFromAddress(context, address.toString());
                                GeoFire geoFire = new GeoFire(appRef.child("BusinessGeoLocation" + "/" + category));
                                geoFire.setLocation(id, new GeoLocation(latLng.latitude, latLng.longitude));
                                //duplication for "all categories" case
                                geoFire = new GeoFire(appRef.child("BusinessGeoLocation/todas"));
                                geoFire.setLocation(id, new GeoLocation(latLng.latitude, latLng.longitude));
                            } catch (NullPointerException e) {
                                Log.e(TAG, "Endereço não cadastrado");
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
