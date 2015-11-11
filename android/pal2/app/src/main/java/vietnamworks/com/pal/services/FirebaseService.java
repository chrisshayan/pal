package vietnamworks.com.pal.services;

import android.content.Context;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/1/15.
 */
public class FirebaseService {
    private static String apiUrl;
    private static String defaultToken;
    public static AuthData authData;
    public static Context context = null;
    public static boolean isConnected;

    public interface OnConnectStatusChanged {
        void onStatusChanged(boolean status);
    }
    private OnConnectStatusChanged onConnectStatusChangedListener;
    private static FirebaseService sInstance = new FirebaseService();


    public interface UserProfileListener {
        void onChanged(HashMap<String, Object> data);
    }
    private UserProfileListener onUserProfileDataChanged;

    Firebase profileRef;
    HashMap<String, Object> userProfile;

    public FirebaseService() {
    }

    public static void SetOnConnectStatusChangedListener(OnConnectStatusChanged listener) {
        sInstance.onConnectStatusChangedListener = listener;
    }

    public static void SetUserProfileListener(UserProfileListener listener) {
        sInstance.onUserProfileDataChanged = listener;
        if (sInstance.userProfile != null && listener != null) {
            listener.onChanged(sInstance.userProfile);
        }
    }

    public static void init(Context ctx) {
        context = ctx;
        apiUrl = context.getString(R.string.firebase_app_url);
        defaultToken = context.getString(R.string.firebase_token);
        isConnected = false;
        Firebase.setAndroidContext(context);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }

    public static Firebase newRef() {
        return new Firebase(apiUrl);
    }

    public static Firebase newRef(String p) {
        String url = apiUrl;
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

    private ValueEventListener publicProfileListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userProfile = dataSnapshot.getValue(HashMap.class);
            if (onUserProfileDataChanged != null) {
                onUserProfileDataChanged.onChanged(userProfile);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private void listenToProfile() {
        if (profileRef == null) {
            profileRef = newRef(Arrays.asList("profiles_pub", authData.getUid()));
            profileRef.addValueEventListener(publicProfileListener);
        }
    }

    public static String getUserProfileStringValue(String key) {
        return getUserProfileStringValue(key, "");
    }

    public static String getUserProfileStringValue(String key, String default_value) {
        String re = null;
        if (sInstance.userProfile != null && sInstance.userProfile.containsKey(key)) {
            Object obj = sInstance.userProfile.get(key);
            if (obj instanceof String) {
                re =  (String)obj;
            }
        }
        if (re == null || re.isEmpty()) {
            re = default_value;
        }
        return re;
    }

    public static int getUserProfileIntValue(String key, int default_value) {
        if (sInstance.userProfile != null && sInstance.userProfile.containsKey(key)) {
            Object obj = sInstance.userProfile.get(key);
            if (obj instanceof Long) {
                return ((Long)obj).intValue();
            } else if (obj instanceof Integer) {
                return (int)obj;
            }
        }
        return default_value;
    }

    public static long getUserProfileLongValue(String key, int default_value) {
        if (sInstance.userProfile != null && sInstance.userProfile.containsKey(key)) {
            Object obj = sInstance.userProfile.get(key);
            if (obj instanceof Long) {
                return (long)obj;
            } else if (obj instanceof Integer) {
                return (long)obj;
            }
        }
        return default_value;
    }

    public static float getUserProfileFloatValue(String key, float default_value) {
        if (sInstance.userProfile != null && sInstance.userProfile.containsKey(key)) {
            Object obj = sInstance.userProfile.get(key);
            if (obj instanceof Float) {
                return (float)obj;
            } else if (obj instanceof Double) {
                return ((Double)obj).floatValue();
            }
        }
        return default_value;
    }


    public static void login(String email, String password, final AsyncCallback callback) {
        newRef().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                FirebaseService.authData = authData;
                if (callback != null) {
                    callback.onSuccess(FirebaseService.context,  null);
                }
                sInstance.listenToProfile();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (callback != null) {
                    callback.onError(FirebaseService.context, firebaseError.getCode(), firebaseError.getMessage());
                }
            }
        });
    }

    public static boolean checkAuthSync() {
        AuthData authData = newRef().getAuth();
        if (authData != null) {
            FirebaseService.authData = authData;
            sInstance.listenToProfile();
            return true;
        } else {
            return false;
        }
    }

    public static void logout() {
        newRef().unauth();
        if (sInstance.profileRef != null) {
            sInstance.profileRef.removeEventListener(sInstance.publicProfileListener);
            sInstance.profileRef = null;
            sInstance.userProfile = null;
            sInstance.onUserProfileDataChanged = null;
        }

    }

    public static String getDefaultToken() {
        return defaultToken;
    }
}
