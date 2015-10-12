package vietnamworks.com.pal.models;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;


/**
 * Created by duynk on 9/16/15.
 */
public class Topics extends AbstractContainer<Topic> {
    boolean isLoading = false;
    protected Topics(){
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
                        topic.setId(postSnapshot.getKey());
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
        }
        super.loadAsync(context, callback);
    }

    @Override
    public void loadAsync(final Context context, Map<String, Object> params, final AsyncCallback callback) {
        if (!isLoading) {
            isLoading = true;
            Boolean useAudioTask = (Boolean) params.get("audio");
            if (useAudioTask) {
                isLoading = false;
                loadAsync(context, callback);
            } else {
                FirebaseService.newRef("topics").orderByChild("type").startAt(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        data.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Topic topic = postSnapshot.getValue(Topic.class);
                            data.add(topic);
                            topic.setId(postSnapshot.getKey());
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
            }
        }
    }
}
