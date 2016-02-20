package kilombu.kilombuapp;

import com.firebase.client.FirebaseError;

/**
 * Created by kizzyterra on 20/02/16.
 */
public class Utils {

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
}
