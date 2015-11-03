package vietnamworks.com.pal.models;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
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
}
