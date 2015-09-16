package vietnamworks.com.pal.models;

import java.util.ArrayList;

/**
 * Created by duynk on 9/16/15.
 */
public class TopicData extends AbstractContainer<Topic> {
    protected TopicData(){
        super();
        ArrayList<Topic> data = new ArrayList<>();
        this.setData(data);
    }

    @Override
    public void loadAsync(OnLoadAsyncCallback callback) {
        ArrayList<Topic> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Topic entity = new Topic();
            entity.mTitle = "Test " + i;
            data.add(entity);
        }
        this.setData(data);
        if (callback != null) {
            callback.onSuccess();
        }
    }
}
