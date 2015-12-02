package vietnamworks.com.pal.models;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/3/15.
 */
public class CurrentUserProfile extends AbstractContainer<CurrentUserProfile> {
    protected CurrentUserProfile(){
        super();
        ArrayList<CurrentUserProfile> data = new ArrayList<>();
        this.setData(data);
    }

    public static void getUserProfile(String uid, final Context ctx, final AsyncCallback callback) {
        FirebaseService.newRef(Arrays.asList("profiles_pub", uid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(ctx, dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void increaseNumOfPost() {
        FirebaseService.newRef(Arrays.asList("profiles_pub", FirebaseService.getUid(), "total_posts")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void updateAvatar(String avatar) {
        FirebaseService.newRef(Arrays.asList("profiles_pub", FirebaseService.getUid(), "avatar")).setValue(avatar);
    }

    public static void updateBasicProfile(String fistName, String lastName, String displayName, String job) {
        HashMap<String, Object> data = new HashMap<>();
        if (fistName != null && !fistName.isEmpty()) {
            data.put("first_name", fistName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            data.put("last_name", lastName);
        }

        if (displayName != null && !displayName.isEmpty()) {
            data.put("display_name", displayName);
        }

        data.put("job_title", job);
        data.put("email", FirebaseService.getUserProfileStringValue("email"));

        FirebaseService.newRef(Arrays.asList("profiles_pub", FirebaseService.getUid())).updateChildren(data, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println(firebaseError.getMessage());
                }
            }
        });
    }

    public static void increaseSessionCounter() {
        int totalSessions = FirebaseService.getUserProfileIntValue("total_sessions", 0);
        totalSessions++;
        FirebaseService.newRef(Arrays.asList("profiles_pub", FirebaseService.getUid(), "total_sessions")).setValue(totalSessions);
    }
}
