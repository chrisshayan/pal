package vietnamworks.com.pal.models;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/3/15.
 */
public class UserProfiles extends AbstractContainer<UserProfiles> {
    protected UserProfiles(){
        super();
        ArrayList<UserProfiles> data = new ArrayList<>();
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
                if(mutableData.getValue() == null) {
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
}
