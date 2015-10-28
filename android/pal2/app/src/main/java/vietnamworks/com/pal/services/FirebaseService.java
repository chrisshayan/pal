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

import vietnamworks.com.pal.configurations.FirebaseSettings;

/**
 * Created by duynk on 10/1/15.
 */
public class FirebaseService {
    public static final String API_URL = FirebaseSettings.APP_URL;
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

    public static void init() {
        isConnected = false;
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        /*
        Firebase connectedRef = new Firebase(API_URL + "/.info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                isConnected = connected;
                if (sInstance.onConnectStatusChangedListener != null) {
                    sInstance.onConnectStatusChangedListener.onStatusChanged(connected);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        */

        /*
        Firebase ref = new Firebase(API_URL);
        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    FirebaseService.authData = authData;
                } else {
                    // user is not logged in
                }
            }
        });
        */
    }

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

    public static String GetUserProfileStringValue(String key) {
        return GetUserProfileStringValue(key, "");
    }

    public static String GetUserProfileStringValue(String key, String default_value) {
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

    public static int GetUserProfileIntValue(String key, int default_value) {
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

    public static float GetUserProfileFloatValue(String key, float default_value) {
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
}
