package vietnamworks.com.pal.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        return FirebaseService.newRef(Arrays.asList("user_quests", FirebaseService.getUid())).orderByChild("index");
    }

    public static Query getRandomTopicQuery() {
        return FirebaseService.newRef(Arrays.asList("user_quests", FirebaseService.getUid())).orderByChild("index").limitToFirst(1);
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

    public static void requestRandomTopics() {
        HashMap<String, Object>obj = new HashMap<>();
        obj.put("user_id", FirebaseService.getUid());
        obj.put("action", "request");
        FirebaseService.newRef(Arrays.asList("quest_queue", "tasks")).push().setValue(obj, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("quest_queue error");
                } else {
                    System.out.println("quest_queue done");
                }
            }
        });
    }
}
