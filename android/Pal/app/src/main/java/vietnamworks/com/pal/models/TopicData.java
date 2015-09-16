package vietnamworks.com.pal.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import vietnamworks.com.pal.services.BaseService;


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
    public synchronized void  loadAsync(Context context, final OnLoadAsyncCallback callback) {
        if (!isLoading) {
            isLoading = true;
            BaseService.Get(context, "tasks", new BaseService.OnLoadAsyncCallback() {
                @Override
                public void onSuccess(JSONObject obj) {
                    try {
                        JSONArray json_data = obj.getJSONArray("data");
                        data.clear();
                        for (int i = 0; i < json_data.length(); i++) {
                            Topic entity = new Topic();
                            entity.mTitle = json_data.getJSONObject(i).getString("question");
                            data.add(entity);
                        }
                        AppModel.topics.setData(data);
                        if (callback != null) {
                            callback.onSuccess();
                        }
                        isLoading = false;
                    } catch (Exception E) {
                        if (callback != null) {
                            callback.onError();
                        }
                        isLoading = false;
                    }
                }

                @Override
                public void onError() {
                    if (callback != null) {
                        callback.onError();
                    }
                    isLoading = false;
                }
            });
        }
    }
}
