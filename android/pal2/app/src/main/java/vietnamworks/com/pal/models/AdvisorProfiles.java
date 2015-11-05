package vietnamworks.com.pal.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;

import java.util.Arrays;
import java.util.HashMap;

import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/5/15.
 */
public class AdvisorProfiles extends  AbstractContainer<AdvisorProfiles> {

    private static void updateAdvisorRate(String advisor, int from, int to) {
        if (from > 0) {
            FirebaseService.newRef(Arrays.asList("profiles_pub", advisor, "rate" + from)).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        mutableData.setValue(0);
                    } else {
                        long v = (Long) mutableData.getValue() - 1;
                        mutableData.setValue(Math.max(v, 0));
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }

        FirebaseService.newRef(Arrays.asList("profiles_pub", advisor, "rate" + to)).runTransaction(new Transaction.Handler() {
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

    public static void vote(final String advisor, final int vote, final String message) {
        HashMap<String, Object> vote_detail = new HashMap<>();
        String user_id = FirebaseService.authData.getUid();
        vote_detail.put("rate", vote);
        vote_detail.put("created_date", Utils.getMillis());
        vote_detail.put("message", message);
        vote_detail.put("display_name", FirebaseService.getUserProfileStringValue("display_name"));
        vote_detail.put("avatar", FirebaseService.getUserProfileStringValue("avatar"));

        FirebaseService.newRef(Arrays.asList("advisor_votes_log", advisor, user_id)).push().setValue(vote_detail);



        HashMap<String, Object> vote_info = (HashMap<String, Object> )vote_detail.clone();
        vote_info.remove("rate");
        FirebaseService.newRef(Arrays.asList("advisor_votes", advisor, user_id)).updateChildren(vote_info);

        FirebaseService.newRef(Arrays.asList("advisor_votes", advisor, user_id, "rate")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(vote);
                    updateAdvisorRate(advisor, 0, vote);
                } else {
                    int last_vote = 0;
                    Object value = mutableData.getValue();
                    if (value instanceof Integer) {
                        last_vote = (Integer) value;
                    } else if (value instanceof Long) {
                        last_vote = ((Long) value).intValue();
                    }
                    updateAdvisorRate(advisor, last_vote, vote);
                    mutableData.setValue(vote);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static Query getRecentComments(final String advisor) {
        return FirebaseService.newRef(Arrays.asList("advisor_votes", advisor)).orderByChild("created_date").startAt(0).limitToLast(5);
    }
}
