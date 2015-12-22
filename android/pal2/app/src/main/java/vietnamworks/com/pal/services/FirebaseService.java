package vietnamworks.com.pal.services;

import android.content.Context;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Logger;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import vietnamworks.com.pal.BuildConfig;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/1/15.
 */
public class FirebaseService {
    public final static int STATUS_UNKNOWN = 0;
    public final static int STATUS_ONLINE = 1;
    public final static int STATUS_OFFLINE = 2;
    static int connectedStatus = STATUS_UNKNOWN;

    private static String apiUrl;
    public static AuthData authData;
    public static Context context = null;
    public static boolean isConnected;

    private static FirebaseService sInstance = new FirebaseService();


    public interface UserProfileListener {
        void onChanged(HashMap<String, Object> data);
    }
    private UserProfileListener onUserProfileDataChanged;

    public interface ConnectionListener {
        void onChanged(int now, int last);
    }
    private ConnectionListener onConnectionChanged;


    Firebase profileRef;
    Firebase connectStatusQuery;
    Firebase root;
    HashMap<String, Object> userProfile;

    public FirebaseService() {
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
        isConnected = false;
        Firebase.setAndroidContext(context);
        if (BuildConfig.DEBUG) {
            Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
        }
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        sInstance.root = newRef();

        sInstance.connectStatusQuery = newRef(".info/connected");
        sInstance.connectStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                int lastStatus = connectedStatus;
                if (connected) {
                    connectedStatus = STATUS_ONLINE;
                } else {
                    connectedStatus = STATUS_OFFLINE;
                }
                if (lastStatus != connectedStatus && sInstance.onConnectionChanged != null) {
                    sInstance.onConnectionChanged.onChanged(connectedStatus, lastStatus);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        checkAuthSync();
    }

    public static Firebase newRef() {
        if (sInstance.root == null) {
            return new Firebase(apiUrl);
        } else {
            return sInstance.root;
        }
    }

    public static Firebase newRef(String p) {
        String[] parts = p.split("/");
        return newRef(Arrays.asList(parts));
    }

    public static Firebase newRef(List<String> path) {
        Firebase p = sInstance.root;
        for(int i = 0; i < path.size(); i++) {
            p = p.child(path.get(i));
        }
        return p;
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

    public static HashMap<String, Object> getPublicProfile() {
        return sInstance.userProfile;
    }


    public static void login(String email, String password, final AsyncCallback callback) {
        newRef().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                FirebaseService.authData = authData;
                if (callback != null) {
                    callback.onSuccess(FirebaseService.context, null);
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

    public static void resetPassword(String email, final AsyncCallback callback) {
        newRef().resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                callback.onSuccess(FirebaseService.context, null);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                callback.onError(FirebaseService.context, firebaseError.getCode(), firebaseError.getMessage());
            }
        });
    }

    public static boolean checkAuthSync() {
        Firebase.goOnline();
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

    public static void setOnConnectionChanged(ConnectionListener onConnectionChanged) {
        sInstance.onConnectionChanged = onConnectionChanged;
        if (onConnectionChanged!= null) {
            onConnectionChanged.onChanged(connectedStatus, STATUS_UNKNOWN);
        }
    }

    public static int getConnectedStatus() {
        return connectedStatus;
    }

    public static boolean isConnected() {
        return connectedStatus == STATUS_ONLINE;
    }

    public static void goOnline() {
        Firebase.goOnline();
    }

    public static String getUid() {
        if (authData != null) {
            if (authData.getUid() == null || authData.getUid().isEmpty()) {
                ExceptionReportService.report("authData.getUid() is null or empty");
            }
            return authData.getUid();
        } else {
            ExceptionReportService.report("authData is null");
            return null;
        }
    }
}
