package vietnamworks.com.pal.services;

import android.content.Context;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;

/**
 * Created by duynk on 10/1/15.
 */
public class FirebaseService {
    public static final String API_URL = "https://pal-dev.firebaseio.com";
    public static AuthData authData;
    public static Context context = null;

    public static void setContext(Context ctx) {
        if (ctx != null) {
            Firebase.setAndroidContext(ctx);
        }
        context = ctx;
    }

    public static Firebase newRef() {
        return new Firebase(API_URL);
    }

    public static Firebase newRef(String p) {
        String url = API_URL;
        if (p.length() > 0) {
            url = url + "/" + p;
        }
        return new Firebase(url);
    }

    public static Firebase newRef(List<String> path) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < path.size(); i++) {
            str.append(path.get(i));
            if (i < path.size() - 1) {
                str.append("/");
            }
        }
        return newRef(str.toString());
    }

    public static void login(String email, String password, final AsyncCallback callback) {
        newRef().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                FirebaseService.authData = authData;
                if (callback != null) {
                    callback.onSuccess(FirebaseService.context,  null);
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (callback != null) {
                    callback.onError(FirebaseService.context, firebaseError.getCode(), firebaseError.getMessage());
                }
            }
        });
    }
}
