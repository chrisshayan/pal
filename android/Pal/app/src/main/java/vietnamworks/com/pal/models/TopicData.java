package vietnamworks.com.pal.models;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;


/**
 * Created by duynk on 9/16/15.
 */
public class TopicData extends AbstractContainer<Topic> {
    boolean isLoading = false;
    protected TopicData(){
        super();
        ArrayList<Topic> data = new ArrayList<>();
        this.setData(data);
    }

    @Override
    public synchronized void loadAsync(final Context context, final AsyncCallback callback) {
        if (!isLoading) {
            isLoading = true;

            FirebaseService.newRef("topics").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    data.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Topic topic = postSnapshot.getValue(Topic.class);
                        data.add(topic);
                    }
                    AppModel.topics.setData(data);
                    if (callback != null) {
                        callback.onSuccess(context, null);
                    }
                    isLoading = false;
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    if (callback != null) {
                        callback.onError(context, 0, "");
                    }
                    isLoading = false;
                }
            });
        } else {
            if (callback != null) {
                callback.onError(context, 0, "");
            }
        }
    }
}
