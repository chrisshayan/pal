package vietnamworks.com.pal.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;

import java.util.ArrayList;
import java.util.Arrays;

import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.services.FirebaseService;


/**
 * Created by duynk on 9/16/15.
 */
public class Topics extends AbstractContainer<Topic> {
    protected Topics(){
        super();
        ArrayList<Topic> data = new ArrayList<>();
        this.setData(data);
    }
    public static Query getAllTopicsQuery() {
        return FirebaseService.newRef("topics").orderByChild("status").equalTo(1);
    }

    public static void addView(String postId) {
        FirebaseService.newRef(Arrays.asList("topics", postId, "views")).runTransaction(new Transaction.Handler() {
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

    public static void addSubmit(String postId) {
        FirebaseService.newRef(Arrays.asList("topics", postId, "submits")).runTransaction(new Transaction.Handler() {
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
